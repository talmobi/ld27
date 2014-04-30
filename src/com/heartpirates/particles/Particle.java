package com.heartpirates.particles;

import com.heartpirates.Factory;
import com.heartpirates.Sprite;

public class Particle extends Sprite {

	protected long birthTime;
	protected long lifeTime = 1000 * 10; // 10 seconds

	public Particle(Factory factory) {
		super(factory);
		birthTime = System.currentTimeMillis();
	}

	@Override
	public void step() {
		super.step();

		if (!removed && System.currentTimeMillis() - birthTime > lifeTime) {
			removed = true;
		}
		
		move();
	}

	protected void move() {
		x += xspeed;
		y += yspeed;
	}

	@Override
	public void addTime(long time) {
		super.addTime(time);
		this.birthTime += time;
	}

	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	protected boolean isBlocked(double x, double y) {
		return level.isBlocked((int) x, (int) y);
	}
}
