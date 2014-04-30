package com.heartpirates.particles;

import java.util.Random;

import com.heartpirates.Factory;

public class JetPackFire extends Particle {

	public JetPackFire(Factory factory) {
		super(factory);

		Random r = this.factory.getRandom();
		this.yspeed = r.nextDouble() * 2.0
				+ this.factory.getPlayer().getYSpeed() * 0.6;
		this.xspeed = 1 - r.nextDouble() * 2.0;
		this.xspeed *= 0.2;
		int n = r.nextInt(4);
		this.setBitmap(this.factory.getArt().tiles[n][2]);
		this.yflip = r.nextBoolean();
		this.xflip = r.nextBoolean();
		this.lifeTime = 1000;
	}

	@Override
	protected void move() {
		super.move();

		this.yspeed -= 0.004;
		
		this.xspeed *= 0.9;
		this.yspeed *= 0.9;
		
		if (isBlocked(this.x, this.y)) {
			this.xspeed = 0;
			this.yspeed = 0;
		}
	}

}