package com.heartpirates;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

import javax.swing.JFrame;

public class Main extends Canvas implements Runnable {

	public final static int FPS = 30;
	public final static int WIDTH = 200;
	public final static int HEIGHT = (WIDTH * 9) / 16;

	public final static int SCALE = 3;
	public final static int SCREEN_WIDTH = WIDTH * SCALE;
	public final static int SCREEN_HEIGHT = HEIGHT * SCALE;

	BufferedImage graybimg = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	BufferedImage bimg = new BufferedImage(WIDTH, HEIGHT,
			BufferedImage.TYPE_INT_RGB);
	Bitmap screen = new Bitmap(bimg);
	int[] pixels = screen.pixels;

	public boolean running;
	JFrame frame = null;

	// Game related vars
	Factory factory = new Factory(this);
	Camera camera = factory.getCamera();
	Art art = factory.getArt();
	Level level = factory.getLevel();
	Keyboard keyboard = factory.getKeyboard();

	Bitmap bitmap = art.chars[0][0];
	Player player;

	int steps = 0;

	public Main() {
		Dimension size = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.setMaximumSize(size);
		this.setSize(size);
		this.setMinimumSize(size);
		init();
	}

	public Main(JFrame frame) {
		this();
		this.frame = frame;
	}

	private void init() {
		this.addKeyListener(factory.getKeyboard());
		this.addMouseListener(factory.getMouse());
		this.addMouseMotionListener(factory.getMouse());

		player = factory.newPlayer(60, 50);
		level.addUnit(player);
		camera.followUnit(player);
	}

	public void start() {
		running = true;
		new Thread(this).start();
	}

	@Override
	public void run() {

		final long NANOS_IN_SECOND = 1000L * 1000L * 1000L;
		final long FPS = Main.FPS;

		final double INTERVAL = NANOS_IN_SECOND / FPS;

		int steps = 0;
		int frames = 0;
		;
		String infoString = "";

		long secondTimer = System.currentTimeMillis();

		double unprocessed = 0.0;
		double processLimit = 5;

		long lastTime = System.nanoTime();

		while (running) {
			long now = System.nanoTime();
			unprocessed += (now - lastTime) / INTERVAL;
			lastTime = now;

			if (unprocessed > processLimit)
				unprocessed = processLimit;

			boolean shouldRender = false;
			while (unprocessed > 0) {
				unprocessed--;
				step();
				steps++;
				shouldRender = true;
			}

			if (shouldRender) {
				render();
				frames++;
			}

			swap();

			if (System.currentTimeMillis() - secondTimer > 1000) {
				secondTimer += 1000;
				infoString = "steps: " + steps + ", frames: " + frames
						+ ", units: " + level.getUnitCount() + ", mem: "
						+ level.getMemorySize() + ", tp: "
						+ level.getTimePointer() + ", state: "
						+ level.getTimeState();
				System.out.println(infoString);
				if (frame != null)
					this.frame.setTitle(infoString);
				steps = 0;
				frames = 0;
			}

			sleep();
		}
	}

	private void step() {
		steps++;

		level.step();
		if (keyboard.SPACE.isPressed()) {
			player.setPosition(60, 50);
			player.xspeed = 0;
			player.yspeed = 0;
			player.onFloor = false;
		}

		camera.step();
	}

	private void render() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = (int) (8000 * 4 + i / WIDTH);
		}

		level.render(screen, camera.x, camera.y);
		
		if (level.isStopped()) {
			screen.transformTimeStopEffect(level.getTimePointer() >> 2);
			player.renderTo(screen, camera.x, camera.y);
		}
	}

	private void swap() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			if (this.getFocusCycleRootAncestor() != null)
				this.createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();

		// Center the game canvas
		int x = (this.getWidth() - SCREEN_WIDTH) >> 1;
		int y = (this.getHeight() - SCREEN_HEIGHT) >> 1;

		g.fillRect(0, 0, this.getWidth(), this.getHeight());

			g.drawImage(bimg, x, y, SCREEN_WIDTH, SCREEN_HEIGHT, null);

		// Draw room (anti gravity spell)
		if (level.showRoom) {
			BufferedImage bimg = art.roomImage;
			double radius = bimg.getWidth() / 2.0;
			double tr = Player.room_radius;
			double shrinkFactor = (tr / radius) * (radius * SCALE);
			int hh = (bimg.getWidth() >> 1);
			int xx = (int) (player.x - hh) * SCALE + camera.x * SCALE;
			int yy = (int) (player.y - hh + (player.getH() >> 1)) * SCALE
					+ camera.y * SCALE;

			g.drawImage(art.roomImage, (int) (xx + shrinkFactor),
					(int) (yy + shrinkFactor),
					(int) (bimg.getWidth() * SCALE - shrinkFactor * 2),
					(int) (bimg.getHeight() * SCALE - shrinkFactor * 2), null);
		}

		g.dispose();
		bs.show();
	}

	private void sleep() {
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Main game = new Main(frame);
		frame.add(game);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();

		// position the frame
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screen.width - Main.SCREEN_WIDTH - 50, 100);

		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		game.start();
	}
}