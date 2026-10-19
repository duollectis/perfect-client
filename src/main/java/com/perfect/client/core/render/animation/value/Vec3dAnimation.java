package com.perfect.client.core.render.animation.value;

import net.minecraft.util.math.Vec3d;

public class Vec3dAnimation {

	private double speed;

	private final DoubleAnimation xAnimation;
	private final DoubleAnimation yAnimation;
	private final DoubleAnimation zAnimation;

	private boolean defined = false;

	public Vec3dAnimation() {
		xAnimation = new DoubleAnimation(0.0);
		yAnimation = new DoubleAnimation(0.0);
		zAnimation = new DoubleAnimation(0.0);
	}

	public Vec3dAnimation defined(boolean defined) {
		this.defined = defined;
		return this;
	}

	public Vec3d setProgressAndGet(Vec3d target, double progress) {
		double prevSpeed = speed;

		speed(progress);
		Vec3d vec3d = updateAndGet(target);

		speed(prevSpeed);
		return vec3d;
	}

	public Vec3dAnimation speed(double speed) {
		this.speed = speed;

		xAnimation.speed(speed);
		yAnimation.speed(speed);
		zAnimation.speed(speed);
		return this;
	}

	public Vec3d updateAndGet(Vec3d target) {
		if (!defined) {
			xAnimation.set(target.x);
			yAnimation.set(target.y);
			zAnimation.set(target.z);

			defined = true;

			return target;
		}
		else {
			double x = xAnimation.updateAndGet(target.x);
			double y = yAnimation.updateAndGet(target.y);
			double z = zAnimation.updateAndGet(target.z);

			return new Vec3d(x, y, z);
		}
	}
}
