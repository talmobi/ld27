package com.heartpirates;

public class Mob extends Unit {

	private long birthTime;
	private long lifeTime = 1000 * 10; // 10 seconds

	private long lastMove = System.currentTimeMillis();
	private long moveDelay = 200 * 2; // 2 sec

	private long lastJump = System.currentTimeMillis();
	private long jumpDelay = 200 * 2; // 2 sec

	double xmove = 0;

	public Mob(Factory factory) {
		super(factory);
		birthTime = System.currentTimeMillis();
		lifeTime += level.random.nextInt(5000);
		this.color = level.random.nextInt(999999999);
	}

	@Override
	public void step() {
		ai();
		super.step(); // move();
	}

	@Override
	public void addTime(long time) {
		super.addTime(time);
		this.birthTime += time;
		this.lastMove += time;
		this.lastJump += time;
	}

	private void ai() {
		long now = System.currentTimeMillis();

		if (now - lastMove > moveDelay) {
			lastMove = now;
			xmove = level.random.nextDouble() * 3 - 1;
			if (level.random.nextInt(10) < 2)
				xmove = 0;
			moveDelay = 1800 + level.random.nextInt(600);
		}
		if (now - lastJump > jumpDelay) {
			lastJump = now;
			if (onFloor)
				yspeed = -(level.random.nextInt(3));
			jumpDelay = 200 + level.random.nextInt(500);
		}

		if (now - birthTime > lifeTime) {
			removed = true;
		}

		xspeed = xmove;
		if (xmove != 0)
			onFloor = false;
	}

	@Override
	public Unit unitCopy() {
		Mob m = new Mob(this.factory);

		m.x = this.x;
		m.y = this.y;
		m.w = this.w;
		m.h = this.h;

		m.spr_xoff = this.spr_xoff;
		m.spr_yoff = this.spr_yoff;

		m.color = this.color;

		m.bitmap = this.bitmap;

		m.removed = this.removed;

		m.xflip = this.xflip;
		m.yflip = this.yflip;

		m.xspeed = this.xspeed;
		m.yspeed = this.yspeed;
		
		m.xmove = this.xmove;

		m.onFloor = this.onFloor;

		m.xImage = this.xImage;
		m.xImageScrollSpeed = this.xImageScrollSpeed;
		
		m.birthTime = this.birthTime;
		m.lifeTime = this.lifeTime;
		
		return m;
	}

}