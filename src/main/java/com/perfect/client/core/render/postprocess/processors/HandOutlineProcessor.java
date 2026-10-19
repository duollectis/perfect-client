package com.perfect.client.core.render.postprocess.processors;

import com.perfect.client.core.render.postprocess.PostProcessShaders;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.gl.Framebuffer;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.hands;

public class HandOutlineProcessor extends EntityProcessor {

	@Override
	public boolean skipDraw() {
		return !hands.isShaderEnabled();
	}

	@Override
	protected void draw(Framebuffer framebuffer) {
		int lineWidth = hands.getLineWidth();
		float fillOpacity = hands.getFillOpacity();

		Color outlineColor = hands.getOutlineColor().getColor();
		Color fillColor = ColorUtils.applyAlpha(outlineColor, fillOpacity);
		Color glowColor = hands.getGlowColor().getColor();

		int mode = hands.getMode();
		float gradientFactor = hands.getGradientFactor();
		float gradientSpeed = hands.getGradientSpeed();

		float waveDistance = hands.getWaveDistance();
		float waveWidth = hands.getWaveWidth();
		float waveSpeed = hands.getWaveSpeed();

		boolean glow = hands.getGlow();
		float glowMultiplier = hands.getGlowMultiplier();
		float glowSize = hands.getGlowSize();

		PostProcessShaders.MULTI_SHADER.draw(
				framebuffer, outlineColor, fillColor,
				fillOpacity, mode, false, lineWidth,
				gradientFactor, waveDistance, waveWidth, waveSpeed, mode == 2 ? gradientSpeed : 1,
				glow, glowMultiplier, glowSize, glowColor
		);
	}
}
