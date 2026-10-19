package com.perfect.client.core.functional.setting.bundles.costemics;

import com.perfect.client.core.render.box.pattern.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Cosmetics {

	private final boolean shader;
	private final int shaderLineWidth;
	private final float shaderGradientFactor;
	private final float shaderGradientSpeed;
	private final boolean gradient;
	private final int segments;

	private final Pattern pattern;

	public Cosmetics(Cosmetics cosmetics) {
		shader = cosmetics.isShader();
		shaderLineWidth = cosmetics.getShaderLineWidth();
		shaderGradientFactor = cosmetics.getShaderGradientFactor();
		shaderGradientSpeed = cosmetics.getShaderGradientSpeed();
		gradient = cosmetics.isGradient();
		segments = cosmetics.getSegments();
		pattern = cosmetics.getPattern();
	}

	public static Cosmetics createEmpty() {
		return new Cosmetics(false, 0, 0, 0, false, 2, Pattern.NONE);
	}
}
