package com.heartpirates;

public class Sprite {
	protected double x;
	protected double y;
	protected int w;
	protected int h;

	protected int spr_xoff = 0;
	protected int spr_yoff = 0;

	protected int color = 0;

	Bitmap bitmap = null;
	protected boolean removed = false;

	protected final Factory factory;
	protected final Level level;

	protected boolean xflip = false;
	protected boolean yflip = false;

	protected double xspeed;
	protected double yspeed;

	protected boolean onFloor = false;

	protected float xImage = 0f;
	protected float xImageScrollSpeed = 0.2f;

	public Sprite(Factory factory) {
		this.factory = factory;
		this.level = factory.getLevel();
	}

	public void init(Bitmap b) {
		this.bitmap = b;
		this.w = b.w;
		this.h = b.h;
	}

	public void step() {
	}

	public void addTime(long time) {
	}

	public void setBitmap(Bitmap b) {
		this.bitmap = b;
	}

	public void renderTo(Bitmap screen, int x, int y) {
		if (bitmap == null)
			return;
		bitmap.flippedRenderToColorized(screen, x + (int) (this.x) - spr_xoff,
				y + (int) (this.y) - spr_yoff, xflip, yflip, color);
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public Sprite spriteCopy() {
		Sprite s = new Sprite(this.factory);

		s.x = this.x;
		s.y = this.y;
		s.w = this.w;
		s.h = this.h;

		s.spr_xoff = this.spr_xoff;
		s.spr_yoff = this.spr_yoff;

		s.color = this.color;

		s.bitmap = this.bitmap;

		s.removed = this.removed;

		s.xflip = this.xflip;
		s.yflip = this.yflip;

		s.xspeed = this.xspeed;
		s.yspeed = this.yspeed;

		s.onFloor = this.onFloor;

		s.xImage = this.xImage;
		s.xImageScrollSpeed = this.xImageScrollSpeed;

		return s;
	}
}