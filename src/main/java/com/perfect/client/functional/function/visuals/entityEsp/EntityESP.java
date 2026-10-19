package com.perfect.client.functional.function.visuals.entityEsp;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.shader.ShaderType;
import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;


public class EntityESP extends Function {

	private final EntityEspConfig config = new EntityEspConfig(this);

	public EntityESP() {
		super("entity-esp", "Entity ESP", Category.VISUALS);
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getShaderType().get());
	}

	public boolean shouldDraw(Entity entity) {
		if (entity == null) {
			return false;
		}
		if (RenderUtils.shouldSkipEntityRender(entity)) {
			return false;
		}

		Vec3d camera = mc.gameRenderer.getCamera().getPos();
		if (camera.distanceTo(entity.getPos()) > config.getRenderDistance().get()) {
			return false;
		}

		return config.getEntities().contains(entity.getType());
	}

	public int getMode() {
		return config.getShaderType().get().ordinal();
	}

	public MultiColor getGlowColor() {
		if (!config.getGlowColor().isEnabled()) {
			return getOutlineColor();
		}

		return config.getGlowColor().get();
	}

	public MultiColor getFillColor() {
		if (config.getShaderType().get() == ShaderType.DEFAULT) {
			return config.getFillColor().get();
		}

		return config.getWaveColor().get();
	}

	public MultiColor getOutlineColor() {
		if (config.getShaderType().get() == ShaderType.DEFAULT) {
			return config.getOutlineColor().get();
		}

		return config.getWaveOutlineColor().get();
	}

	public boolean getCustomOutline() {
		return config.getWaveOutlineColor().isEnabled();
	}

	public float getWaveDistance() {
		return config.getWaveDistance().getFloat() / 10.0f;
	}

	public float getWaveWidth() {
		return (float) config.getWaveWidth().fromPercent();
	}

	public float getWaveSpeed() {
		return (config.getWaveSpeed().getFloat() * 3.0f) / 20.0f;
	}

	public int getLineWidth() {
		return config.getLineWidth().get();
	}

	public boolean getGradient() {
		return config.isGradientShader();
	}

	public float getGradientFactor() {
		return (float) config.getGradientFactor().fromBPS();
	}

	public float getGradientSpeed() {
		return (float) config.getGradientSpeed().getFloat() / 10f;
	}

	public float getFillOpacity() {
		return (float) config.getFillOpacity().fromPercent();
	}

	public boolean getGlow() {
		return config.getGlow().get();
	}

	public float getGlowMultiplier() {
		return (float) config.getGlowMultiplier().fromPercent();
	}

	public float getGlowSize() {
		return (float) config.getGlowSize().fromPercent();
	}
}

