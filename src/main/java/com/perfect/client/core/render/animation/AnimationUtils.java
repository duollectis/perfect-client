package com.perfect.client.core.render.animation;

import com.perfect.client.core.utils.math.MathUtils;

import static com.perfect.client.PerfectClient.mc;


public class AnimationUtils {

	public static float animateFloat(float oldValue, float newValue, double factor) {
		return (float) animate(oldValue, newValue, factor);
	}

	public static double animate(double oldValue, double newValue, double factor) {
		return MathUtils.interpolate(oldValue, newValue, getAnimationFactor(factor));
	}

	private static double getAnimationFactor(double factor) {
		int fps = Math.max(mc.getCurrentFps(), 1);
		double f = (factor / fps) * 100d;
		return Math.clamp(f, 0, 1);
	}
}
