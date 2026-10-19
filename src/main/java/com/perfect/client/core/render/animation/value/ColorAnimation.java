package com.perfect.client.core.render.animation.value;

import com.perfect.client.core.utils.color.ColorUtils;

import java.awt.*;

public class ColorAnimation {

	private final FloatAnimation rAnimation;
	private final FloatAnimation gAnimation;
	private final FloatAnimation bAnimation;
	private final FloatAnimation aAnimation;

	private boolean defined = false;

	public ColorAnimation() {
		rAnimation = new FloatAnimation(0.0);
		gAnimation = new FloatAnimation(0.0);
		bAnimation = new FloatAnimation(0.0);
		aAnimation = new FloatAnimation(0.0);
	}

	public ColorAnimation defined(boolean defined) {
		this.defined = defined;
		return this;
	}

	public ColorAnimation speed(double speed) {
		rAnimation.speed(speed);
		gAnimation.speed(speed);
		bAnimation.speed(speed);
		return this;
	}

	public Color updateAndGet(Color target) {
		float targetR = target.getRed() / 255.0f;
		float targetG = target.getGreen() / 255.0f;
		float targetB = target.getBlue() / 255.0f;
		float targetA = target.getAlpha() / 255.0f;

		if (!defined) {
			rAnimation.set(targetR);
			gAnimation.set(targetG);
			bAnimation.set(targetB);
			aAnimation.set(targetA);

			defined = true;

			return target;
		}
		else {
			float r = rAnimation.updateAndGet(targetR);
			float g = gAnimation.updateAndGet(targetG);
			float b = bAnimation.updateAndGet(targetB);
			float a = aAnimation.updateAndGet(targetA);

			return ColorUtils.newColor(r, g, b, a);
		}
	}
}
