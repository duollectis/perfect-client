package com.perfect.client.core.render.shader.renderer.round;

import lombok.Getter;

@Getter
public class BlurredRoundBuilder extends ColoredRoundBuilder<BlurredRoundBuilder> {

	protected float blur;

	public BlurredRoundBuilder(IBuiltDraw<BlurredRoundBuilder> drawAction) {
		super(drawAction);
	}

	public BlurredRoundBuilder blur(float blur) {
		this.blur = blur;
		return this;
	}
}
