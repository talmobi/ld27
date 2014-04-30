package com.heartpirates;

import java.util.Random;

import com.heartpirates.particles.JetPackFire;
import com.heartpirates.particles.Particle;

public class Factory {

	private final Main game;

	private Camera camera = null;
	private Art art = null;
	private Mouse mouse = null;
	private Level level = null;
	private Keyboard keyboard = null;

	private Player player = null;

	public Factory(Main game) {
		this.game = game;
	}

	public Keyboard getKeyboard() {
		if (keyboard == null) {
			keyboard = new Keyboard();
		}
		return keyboard;
	}

	public Art getArt() {
		if (art == null) {
			art = new Art();
		}
		return art;
	}

	public Level getLevel() {
		if (level == null) {
			level = new Level(this, game, getArt(), getMouse(), getKeyboard());
			level.load("levels/level.png");
			level.init();
		}
		return level;
	}

	public Mouse getMouse() {
		if (mouse == null) {
			mouse = new Mouse(this);
		}

		return mouse;
	}

	public Player newPlayer(int x, int y) {
		if (player == null) {
			player = new Player(this);
			player.init(getKeyboard(), getMouse());
			player.setPosition(x, y);
			player.setBitmap(getArt().chars[0][0]);
		}

		return player;
	}

	public Player getPlayer() {
		return player;
	}

	public Unit newUnit(int x, int y) {
		Unit u = new Unit(this);
		u.setPosition(x, y);

		// set default sprite
		u.setBitmap(getArt().chars[0][0]);

		return u;
	}

	public Unit newMob(int x, int y) {
		Unit u = new Mob(this);
		u.setPosition(x, y);

		// set default sprite
		u.setBitmap(getArt().chars[0][0]);

		return u;
	}

	public Camera getCamera() {
		if (camera == null) {
			camera = new Camera(Main.WIDTH, Main.HEIGHT, getKeyboard());
		}

		return camera;
	}

	public Random getRandom() {
		return this.level.random;
	}

	public Particle newParticle(int x, int y) {
		Particle p = new JetPackFire(this);
		p.setPosition(x, y);
		this.getLevel().addParticle(p);
		return p;
	}

}