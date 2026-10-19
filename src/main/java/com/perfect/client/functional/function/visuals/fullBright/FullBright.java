package com.perfect.client.functional.function.visuals.fullBright;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import lombok.Getter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBright extends Function {

	@Getter
	private final FullBrightConfig config = new FullBrightConfig(this);

	private int skyBrightness;
	private int blockBrightness;

	private boolean reloadWorldRenderer;

	public FullBright() {
		super("full-bright", "Full Bright", Category.VISUALS);
	}

	@Override
	public void onPreTick() {
		if (mc.player != null && config.getMode().is(FullBrightMode.POTION)) {
			mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 9999, 255));
		}

		if (reloadWorldRenderer) {
			reloadWorldRenderer = false;

			skyBrightness = getBrightness(config.getSkyBrightness().get());
			blockBrightness = getBrightness(config.getBlockBrightness().get());

			if (mc.worldRenderer != null) {
				mc.worldRenderer.reload();
			}
		}
	}

	@Override
	public void onEnable() {
		if (config.getMode().is(FullBrightMode.LUMINANCE)) {
			reloadWorldRenderer = true;
		}
	}

	@Override
	public void onDisable() {
		if (mc.worldRenderer != null && config.getMode().is(FullBrightMode.LUMINANCE)) {
			mc.worldRenderer.reload();
		}

		if (mc.player != null && config.getMode().is(FullBrightMode.POTION)) {
			mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
		}

		reloadWorldRenderer = false;
	}

	public void reloadMode() {
		if (isEnabled() && mc.player != null) {
			reloadWorldRenderer = true;
			mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
		}
	}

	public void reload() {
		reloadWorldRenderer = true;
	}

	public int getLuminance(boolean sky, int original) {
		if (isEnabled() && config.isLuminance()) {
			if (sky && config.isSkyLuminance()) {
				return Math.max(original, skyBrightness);
			}

			if (!sky && config.isBlockLuminance()) {
				return Math.max(original, blockBrightness);
			}
		}

		return original;
	}

	private static int getBrightness(double percent) {
		return (int) Math.ceil(15 * (percent / 100D));
	}

	public enum FullBrightMode {
		LUMINANCE,
		POTION
	}

	public enum LightType {
		SKY,
		BLOCK
	}
}
