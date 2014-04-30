package com.heartpirates;

public class Camera {
	int x = 0;
	int y = 0;

	int width;
	int height;

	Unit target = null;

	int left_offset = 60;
	int right_offset = 60;

	int top_offset = 40;
	int low_offset = 35;

	// int left_offset = 20;
	// int right_offset = 20;
	//
	// int top_offset = 40;
	// int low_offset = 10;

	private Keyboard keyboard;

	public Camera(int width, int height, Keyboard kb) {
		this.width = width;
		this.height = height;
		this.keyboard = kb;
	}

	public void step() {
		move();
	}

	private void move() {
		if (target == null)
			return;

		int tx = target.getX();
		int ty = target.getY();
		int tw = target.getW();
		int th = target.getH();

		int x = -this.x;
		int y = -this.y;

		// Horizontal
		if (x + left_offset > tx)
			this.x = -(tx - left_offset);
		if (x + width - right_offset < tx + tw)
			this.x = -(tx + tw - width + right_offset);

		// Vertical
		if (y + top_offset > ty)
			this.y = -(ty - top_offset);
		if (y + height - low_offset < ty + th)
			this.y = -(ty + th - height + low_offset);
	}

	void updateKeyboard() {
		if (keyboard.UP.isPressed()) {
			y += 1;
		}
		if (keyboard.DOWN.isPressed()) {
			y -= 1;
		}
		if (keyboard.LEFT.isPressed()) {
			x += 1;
		}
		if (keyboard.RIGHT.isPressed()) {
			x -= 1;
		}
	}

	public void followUnit(Unit u) {
		target = u;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}