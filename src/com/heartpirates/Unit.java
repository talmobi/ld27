package com.heartpirates;

public class Unit extends Sprite {

	protected double xspeed;
	protected double yspeed;

	protected boolean onFloor = false;

	protected float xImage = 0f;
	protected float xImageScrollSpeed = 0.2f;

	protected Keyboard keyboard = null;

	public Unit(Factory factory) {
		super(factory);
		init();
	}

	private void init() {
		this.w = 1;
		this.h = 6;
		this.spr_xoff = 3;
		this.spr_yoff = 2;
	}

	@Override
	public void step() {
		super.step();
		move();
	}

	@Override
	public void addTime(long time) {
		super.addTime(time);
	}

	private void move() {
		if (!onFloor)
			yspeed += level.gravity;
		xspeed *= level.friction;

		y += yspeed;
		verticalMovement();

		x += xspeed;
		horizontalMovement();

		if (Math.abs(xspeed) < 0.05 & xspeed != 0) {
			xspeed = 0;
			onFloor = false;
		}

		updateSprite();
	}

	private void verticalMovement() {
		int x = (int) this.x;
		int y = (int) this.y;
		if (yspeed > 0) { // Falling
			if (isBlocked(x, y + h + yspeed)) {
				int j = (int) (y + h + yspeed);
				j = level.nextFloor(x, j);
				this.y = j - h;
				yspeed = 0;
				onFloor = true;
				System.out.println("COLLISION WITH GROUND");
			} else {
				onFloor = false;
			}

		}
		if (yspeed < 0) { // Jumping
			if (isBlocked(x, y)) {
				int j = (int) (y);
				this.y = (j & 0xFFFFFFFE) + level.TILE_HEIGHT;
				yspeed = -(yspeed * 0.3);
				System.out.println("COLLISION WITH CEILING");
			}
			onFloor = false;
		}
	}

	private void horizontalMovement() {
		int x = (int) this.x;
		int y = (int) this.y;
		if (xspeed > 0) {
			if (isBlocked(x + w, y)) {
				int i = (int) (x + w);
				this.x = (i & 0xFFFFFFFE) - w;
				xspeed = 0;
				xImageScrollSpeed = 0.1f;
			} else if (isBlocked(x + w, y + h - 1)) {
				if (!isBlocked(x + w, y + h - 1 - level.TILE_HEIGHT)) {
					if (onFloor) {
						onFloor = false;
						y -= level.TILE_HEIGHT;
					}
				} else {
					int i = (int) (x + w);
					this.x = (i & 0xFFFFFFFE) - w;
					xspeed = 0;
					xImageScrollSpeed = 0.1f;
				}
			} else {
				xImageScrollSpeed = 0.2f;
			}
		} else if (xspeed < 0) {
			if (isBlocked(x, y)) {
				int i = (int) (x);
				this.x = (i & 0xFFFFFFFE) + level.TILE_WIDTH;
				xspeed = 0;
				xImageScrollSpeed = -0.1f;
			}
			if (isBlocked(x, y + h - 1)) {
				if (!isBlocked(x, y + h - 1 - level.TILE_HEIGHT)
						&& !isBlocked(x + 1, y - 1)) {
					if (onFloor) {
						onFloor = false;
						y -= level.TILE_HEIGHT;
					}
				} else {
					int i = (int) (x);
					this.x = (i & 0xFFFFFFFE) + level.TILE_WIDTH;
					xspeed = 0;
					xImageScrollSpeed = -0.1f;
				}
			} else
				xImageScrollSpeed = -0.2f;
		}
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
		onFloor = false;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	private boolean isBlocked(double x, double y) {
		return level.isBlocked((int) x, (int) y);
	}

	private void updateSprite() {
		if (xspeed > 0)
			xflip = false;
		if (xspeed < 0)
			xflip = true;
		xImage += xImageScrollSpeed;
		int n = (int) (xImage % 4);
		while (n < 0)
			n += 4;
		if (xspeed == 0)
			n = 0;
		this.bitmap = level.art.chars[n][0];
	}

	public Unit unitCopy() {
		Unit u = new Unit(this.factory);

		u.x = this.x;
		u.y = this.y;
		u.w = this.w;
		u.h = this.h;

		u.spr_xoff = this.spr_xoff;
		u.spr_yoff = this.spr_yoff;

		u.color = this.color;

		u.bitmap = this.bitmap;

		u.removed = this.removed;

		u.xflip = this.xflip;
		u.yflip = this.yflip;

		u.xspeed = this.xspeed;
		u.yspeed = this.yspeed;

		u.onFloor = this.onFloor;

		u.xImage = this.xImage;
		u.xImageScrollSpeed = this.xImageScrollSpeed;

		return u;
	}
}