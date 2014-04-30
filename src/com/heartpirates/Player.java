package com.heartpirates;

public class Player extends Unit {

	public static double room_radius = (51.0);
	public Keyboard keyboard;
	public Mouse mouse;

	private double jetPackThrust = 0;
	private double jetPackAcceleration = 0.12;
	private double jetPackDeceleration = 0.05;
	private double maxJetPack = 0.20;

	public Player(Factory factory) {
		super(factory);
	}

	public void init(Keyboard keyboard, Mouse mouse) {
		this.keyboard = keyboard;
		this.mouse = mouse;
	}

	@Override
	public void step() {
		updateKeyboard();
		updateMouse();
		jetPackUpdate();
		super.step();
		this.color = level.random.nextInt(0xFFFFFF);
	}

	private void updateMouse() {
		if (mouse.mbuttons[mouse.MRIGHT]) {
			System.out.println("RIGHT MOUSE");
			// right mouse button pressed
			activateJetPacks();
		}
	}

	private void jetPackUpdate() {
		jetPackThrust -= jetPackDeceleration;
		if (jetPackThrust < 0)
			jetPackThrust = 0;

		yspeed -= jetPackThrust;
	}

	private void activateJetPacks() {
		onFloor = false;
		jetPackThrust += jetPackAcceleration;
		if (jetPackThrust > maxJetPack)
			jetPackThrust = maxJetPack;
		this.factory.newParticle((int) this.x, (int) this.y + this.h);
	}

	private void updateKeyboard() {
		if (keyboard == null)
			return;

		if (keyboard.UP.isPressed()) {
			if (onFloor) {
				yspeed = -2;
				onFloor = false;
			}

		}

		if (keyboard.DOWN.isPressed()) {
		}

		if (keyboard.LEFT.isPressed()) {
			xspeed = -1;
			onFloor = false; // recheck if standing on floor
		}

		if (keyboard.RIGHT.isPressed()) {
			xspeed = 1;
			onFloor = false;
		}
	}

	@Override
	public Unit unitCopy() {
		return this;
	}

	public double getYSpeed() {
		return yspeed;
	}

}