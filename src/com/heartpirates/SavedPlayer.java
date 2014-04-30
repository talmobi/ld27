package com.heartpirates;

public class SavedPlayer extends Unit {

	private Player player;

	public SavedPlayer(Factory factory) {
		super(factory);
		removed = false;
	}

	public void init(Player player) {
		this.player = player;
		save(player);
	}

	private void save(Player player) {
		this.x = player.x;
		this.y = player.y;
		this.w = player.w;
		this.h = player.h;

		this.spr_xoff = player.spr_xoff;
		this.spr_yoff = player.spr_yoff;

		this.color = player.color;

		this.bitmap = player.bitmap;

		this.xflip = player.xflip;
		this.yflip = player.yflip;

		this.xspeed = player.xspeed;
		this.yspeed = player.yspeed;

		this.onFloor = player.onFloor;

		this.xImage = player.xImage;
		this.xImageScrollSpeed = player.xImageScrollSpeed;

		this.keyboard = player.keyboard;
	}

	private void load() {
		player.x = this.x;
		player.y = this.y;
		player.w = this.w;
		player.h = this.h;

		player.spr_xoff = this.spr_xoff;
		player.spr_yoff = this.spr_yoff;

		player.color = this.color;

		player.bitmap = this.bitmap;

		player.xflip = this.xflip;
		player.yflip = this.yflip;

		player.xspeed = this.xspeed;
		player.yspeed = this.yspeed;

		player.onFloor = this.onFloor;

		player.xImage = this.xImage;
		player.xImageScrollSpeed = this.xImageScrollSpeed;

		player.keyboard = this.keyboard;
	}

	@Override
	public void step() {
		if (!removed) {
		}
		removed = true;
	}

	@Override
	public void renderTo(Bitmap screen, int x, int y) {
		if (removed)
			return;
		if (level.isStopped()) {
			load();
		}
	}

	@Override
	public Unit unitCopy() {
		return null;
	}

}