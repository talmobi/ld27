package com.heartpirates;

import java.applet.Applet;

public class GameApplet extends Applet {
	private static final long serialVersionUID = 1L;

	private Main game;

	@Override
	public void init() {
		super.init();
		game = new Main();
		this.add(game);
		game.start();
	}

	@Override
	public void start() {
		super.start();
		game.requestFocus();
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void destroy() {
		super.destroy();
	}
}
