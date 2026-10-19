package com.perfect.client.core.functional.setting.bundles.costemics;

import com.perfect.client.core.render.animation.easing.IEasing;
import lombok.Getter;

@Getter
public class AnimatedCosmetics extends Cosmetics {

	private final boolean opacityAnimation;
	private final boolean scaleAnimation;
	private final IEasing easing;
	private final int animationTimeMillis;

	public AnimatedCosmetics(
			Cosmetics cosmetics,
			boolean opacityAnimation,
			boolean scaleAnimation,
			IEasing easing,
			int animationTimeMillis
	) {
		super(cosmetics);

		this.opacityAnimation = opacityAnimation;
		this.scaleAnimation = scaleAnimation;
		this.easing = easing;
		this.animationTimeMillis = animationTimeMillis;
	}

	public boolean isAnimation() {
		return scaleAnimation || opacityAnimation;
	}
}
