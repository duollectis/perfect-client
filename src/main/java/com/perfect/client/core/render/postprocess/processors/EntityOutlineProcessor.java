package com.perfect.client.core.render.postprocess.processors;

import com.perfect.client.core.render.postprocess.PostProcessShaders;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.entity.Entity;

import java.awt.*;

import static com.perfect.client.manager.system.FunctionManager.entityESP;

public class EntityOutlineProcessor extends EntityProcessor {

	@Override
	public boolean skipDraw() {
		return !entityESP.isEnabled();
	}

	@Override
	public boolean shouldDraw(Entity entity) {
		if (skipDraw()) {
			return false;
		}

		return entityESP.shouldDraw(entity);
	}

	@Override
	protected void draw(Framebuffer framebuffer) {
		int lineWidth = entityESP.getLineWidth();

		Color outlineColor = entityESP.getOutlineColor().getColor();
		Color fillColor = entityESP.getFillColor().getColor();
		Color glowColor = entityESP.getGlowColor().getColor();
		float fillOpacity = entityESP.getFillOpacity();

		int mode = entityESP.getMode();
		boolean customOutline = entityESP.getCustomOutline();
		float gradientFactor = entityESP.getGradientFactor();
		float gradientSpeed = entityESP.getGradientSpeed();

		float waveDistance = entityESP.getWaveDistance();
		float waveWidth = entityESP.getWaveWidth();
		float waveSpeed = entityESP.getWaveSpeed();

		boolean glow = entityESP.getGlow();
		float glowMultiplier = entityESP.getGlowMultiplier();
		float glowSize = entityESP.getGlowSize();

		PostProcessShaders.MULTI_SHADER.draw(
				framebuffer, outlineColor, fillColor,
				fillOpacity, mode, customOutline, lineWidth,
				gradientFactor, waveDistance, waveWidth, waveSpeed, mode == 2 ? gradientSpeed : 1,
				glow, glowMultiplier, glowSize, glowColor
		);
	}
}
