package com.heartpirates;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import com.heartpirates.particles.Particle;

public class Level {

	private final Factory factory;
	private final Main game;
	public final Art art;
	public final Mouse mouse;
	public final Keyboard keyboard;

	public boolean showRoom = false;

	public Random random = new Random();

	Bitmap level = null;
	private boolean addPlayerSwitch;

	public final double gravity = 0.1;
	public final double friction = 0.7;

	public final int TILE_WIDTH = 2;
	public final int TILE_HEIGHT = 2;

	public final int TILE_WIDTH_SHIFT = 1;
	public final int TILE_HEIGHT_SHIFT = 1;
	boolean[][] blockMap = null;

	private long testTime = System.currentTimeMillis();
	private long testDelay = 400;

	List<Sprite> sprites = new ArrayList<Sprite>(32);
	List<Sprite> spritesSwap = new ArrayList<Sprite>(32);
	List<Unit> addUnitList = new LinkedList<Unit>();

	List<Sprite> particleList = new ArrayList<Sprite>();
	List<Sprite> addParticleList = new ArrayList<Sprite>();

	List<Random> rngMemory = new LinkedList<Random>();
	List<Long> timeMemory = new LinkedList<Long>();
	private long oldTime = 0;
	List<LinkedList<Sprite>> spriteMemory = new LinkedList<LinkedList<Sprite>>();
	final int MAX_MEMORY = Main.FPS * 10; // 10 seconds of lists

	private int timePointer = 0;

	private long pausedAt = System.currentTimeMillis();

	// Time stuff
	private enum TimeState {
		PLAY, STOP, PAUSE, UNPAUSE
	}

	TimeState timeState = TimeState.PLAY;

	public Level(Factory factory, Main game, Art art, Mouse mouse,
			Keyboard keyboard) {
		this.factory = factory;
		this.game = game;
		this.art = art;
		this.mouse = mouse;
		this.keyboard = keyboard;
	}

	public void load(String location) {
		URL url = this.getClass().getClassLoader().getResource(location);

		BufferedImage bimg = null;
		try {
			bimg = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Couldn't load Level!");
			System.exit(0);
		}

		level = new Bitmap(bimg);
	}

	public void init() {
		if (level == null) {
			System.out.println("Can't initialize, level is NULL!");
			return;
		}

		blockMap = new boolean[level.w][level.h];

		for (int i = 0; i < level.pixels.length; i++) {
			int color = level.pixels[i];

			int x = i % level.w;
			int y = i / level.w;

			if ((color & 0xFF000000) == 0)
				blockMap[x][y] = false;
			else
				blockMap[x][y] = true;
		}
	}

	public void step() {
		updateMouse();
		updateKeyboard();

		switch (timeState) {
		case PLAY:
			play();
			saveUnits();
			break;
		case STOP:
			break;
		case UNPAUSE:
			long now = System.currentTimeMillis();
			long delta = now - oldTime;
			for (Sprite s : sprites) {
				s.addTime(delta);
			}
			oldTime = 0;

			while (spriteMemory.size() > timePointer) {
				int pos = spriteMemory.size() - 1;
				if (spriteMemory.get(pos) == sprites) {
					System.out.println("---BREAKING---");
					break;
				} else {
					System.out.println("    ---- ");
					spriteMemory.remove(pos);
					rngMemory.remove(pos);
					timeMemory.remove(pos);
				}
			}

			timeState = TimeState.PLAY;
			break;
		case PAUSE:
			saveUnits();
			timeState = TimeState.STOP;
			break;
		default:
			break;
		}

		synchronized (addUnitList) {
			for (Unit u : addUnitList) {
				sprites.add(u);
			}
			addUnitList.clear();
		}

		synchronized (addParticleList) {
			for (Sprite p : addParticleList) {
				sprites.add(p);
			}
			addParticleList.clear();
		}

	}

	private void saveUnits() {
		while (spriteMemory.size() >= MAX_MEMORY) {
			spriteMemory.remove(0); // remove first list
			rngMemory.remove(0);
			timeMemory.remove(0);
		}
		rngMemory.add(getRandomCopy());
		timeMemory.add(System.currentTimeMillis());
		LinkedList<Sprite> list = new LinkedList<Sprite>();

		for (Sprite spr : sprites) {
			if (spr.removed || !(spr instanceof Unit)
					|| (spr instanceof SavedPlayer))
				continue;
			Unit u = (Unit) spr;
			Sprite copy = u.unitCopy();
			list.add(copy);
			if (u instanceof Player) {
				SavedPlayer sp = new SavedPlayer(this.factory);
				sp.init(factory.getPlayer());
				list.add(sp);
			}
		}

		spriteMemory.add(list);
		timePointer++;
		if (timePointer > MAX_MEMORY)
			timePointer = MAX_MEMORY;
	}

	private Random getRandomCopy() {
		Random r = null;

		try {
			PipedOutputStream pout = new PipedOutputStream();
			PipedInputStream pin = new PipedInputStream(pout);

			ObjectOutputStream oos = new ObjectOutputStream(pout);
			ObjectInputStream ois = new ObjectInputStream(pin);

			oos.writeObject(this.random);
			pout.close();
			oos.close();

			r = (Random) ois.readObject();
			pin.close();
			ois.close();
			// System.out.println("Copied Random. matched ["
			// + (r.nextInt() == this.random.nextInt() ? true : false)
			// + "]");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return r;
	}

	public int getMemorySize() {
		return spriteMemory.size();
	}

	private void play() {
		pausedAt = System.currentTimeMillis();

		synchronized (sprites) {
			for (Sprite spr : sprites) {
				if (spr.removed)
					continue;
				spr.step();
			}
		}
	}

	private void updateKeyboard() {
		showRoom = keyboard.VK_2.isPressed();

		if (keyboard.VK_1.isPressed()) {
			if (timeState == TimeState.STOP) {
				timeState = TimeState.UNPAUSE;
			}
		}
		if (keyboard.VK_3.isPressed()) {
			if (timeState == TimeState.PLAY) {
				timeState = TimeState.PAUSE;
			}
		}
		if (keyboard.VK_Q.isPressed() && timeState == TimeState.STOP) {
			// reverse
			System.out.println("reversing");

			timePointer--;
			if (timePointer < 2)
				timePointer = 1;

			loadMemory(timePointer);
		}
		if (keyboard.VK_E.isPressed() && timeState == TimeState.STOP) {
			// reverse
			System.out.println("forwarding");

			timePointer++;
			if (timePointer >= MAX_MEMORY)
				timePointer = MAX_MEMORY - 1;
			if (timePointer >= spriteMemory.size())
				timePointer = spriteMemory.size() - 1;

			loadMemory(timePointer);
		}
	}

	private void loadMemory(int pos) {
		if (pos < 2 || pos >= MAX_MEMORY - 1 || pos >= spriteMemory.size() - 1)
			return;
		if (spriteMemory.size() > 0) {
			try {
				// sprites.clear();

				sprites = spriteMemory.get(pos);
				random = rngMemory.get(pos);
				long now = System.currentTimeMillis();
				oldTime = timeMemory.get(timePointer);
				pausedAt = now;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void updateMouse() {
		if (mouse.mbuttons[mouse.MLEFT]) {
			if (timeState == TimeState.PLAY) {
				System.out.println("MOUSE LEFT PRESSED");
				addUnit(factory.newMob(mouse.getX(), mouse.getY()));
			}
		}
	}

	public void render(Bitmap screen, int x, int y) {
		renderLevel(screen, x, y);
		renderSprites(screen, x, y);
	}

	private void renderLevel(Bitmap screen, int x, int y) {
		for (int i = 0; i < level.w; i++) {
			for (int j = 0; j < level.h; j++) {
				if (blockMap[i][j] == false)
					continue;

				art.tiles[0][0].renderTo(screen, x + i * TILE_WIDTH, y + j
						* TILE_HEIGHT);
			}
		}
	}

	private void renderSprites(Bitmap screen, int x, int y) {
		try {
			for (Sprite spr : sprites) {
				if (spr.removed)
					continue;
				spr.renderTo(screen, x, y);
				spritesSwap.add(spr);
			}

			List<Sprite> t = sprites;
			sprites = spritesSwap;
			spritesSwap = t;
			spritesSwap.clear();
		} catch (ConcurrentModificationException cme) {
		}
	}

	public void renderTo(Bitmap screen, int x, int y) {
		if (level != null)
			level.renderTo(screen, x, y);
	}

	public boolean isBlocked(int x, int y) {
		int i = x >> TILE_WIDTH_SHIFT;
		int j = y >> TILE_HEIGHT_SHIFT;

		// check out of bounds
		if (i < 0 || j < 0 || i >= level.w || j >= level.h)
			return false;

		return blockMap[i][j];
	}

	public int nextFloor(int x, int y) {
		int i = x >> TILE_WIDTH_SHIFT;
		int j = y >> TILE_HEIGHT_SHIFT;

		// check out of bounds
		if (i < 0 || j < 0 || i >= level.w || j >= level.h)
			return j;

		while (j > 0) {
			if (!blockMap[i][j])
				break;
			j--;
		}

		return (j + 1) << TILE_HEIGHT_SHIFT;
	}

	public void addUnit(Unit unit) {
		synchronized (addUnitList) {
			addUnitList.add(unit);
		}
	}

	public void addParticle(Particle p) {
		synchronized (addParticleList) {
			addParticleList.add(p);
		}
	}

	public void addPlayer() {
		addPlayerSwitch = true;
		System.out.println("ADD PLAYER!!!");
	}

	public int getUnitCount() {
		return sprites.size();
	}

	private void spawnRandomMobs() {
		long now = System.currentTimeMillis();

		if (now - testTime > testDelay) {
			testTime = now;
			addUnit(factory.newMob(
					factory.getPlayer().getX() + random.nextInt(50) + 10,
					factory.getPlayer().getY() - 15));

			if (random.nextInt(50) < 20) {
				int num = 20;
				if (random.nextInt(100) < 10) { // spawn lots
					num = 600;
				}
				for (int i = 0; i < num; i++) {
					addUnit(factory.newMob(factory.getPlayer().getX() + i * 2
							- num / 2, factory.getPlayer().getY() - 40));
				}
			}
		}
	}

	public int getTimePointer() {
		return timePointer;
	}

	public boolean isStopped() {
		return (timeState == TimeState.STOP);
	}

	public String getTimeState() {
		return timeState.toString();
	}

}