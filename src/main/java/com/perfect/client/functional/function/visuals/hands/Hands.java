package com.perfect.client.functional.function.visuals.hands;

import com.perfect.client.core.event.events.render.HeldItemRendererEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.shader.ShaderType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;


public class Hands extends Function {

	private final HandsConfig config = new HandsConfig(this);

	public Hands() {
		super("hands", "Hands", Category.VISUALS, "Hand View");
	}

	private static boolean mainSwinging = false;
	private float mainProgress = 0;

	private boolean offSwinging = false;
	private float offProgress = 0;

	public void startSwing(Hand hand) {
		if (!isEnabled()) {
			return;
		}

		boolean reset = config.getResetSwingAnimation().get();

		if (hand == Hand.MAIN_HAND) {
			if (reset || !mainSwinging) {
				mainProgress = 0;
				mainSwinging = true;
			}
		} else {
			if (reset || !offSwinging) {
				offProgress = 0;
				offSwinging = true;
			}
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mainSwinging) {
			if (mainProgress >= 1) {
				mainSwinging = false;
				mainProgress = 0;
			} else {
				mainProgress += config.getSwingSpeed().get() / 10000.0f;
			}
		}

		if (offSwinging) {
			if (offProgress >= 1) {
				offSwinging = false;
				offProgress = 0;
			} else {
				offProgress += config.getSwingSpeed().get() / 10000.0f;
			}
		}
	}

	public float getSwing(Hand hand) {
		return (float) config.getSwingEnd().fromPercent() * (hand == Hand.MAIN_HAND ? mainProgress : offProgress);
	}

	public float getY(Hand hand) {
		return (float) (config.getX().get() + (config.getSwingEndY().get() - config.getX().get()) * (hand == Hand.MAIN_HAND ? mainProgress : offProgress)) / -10f;
	}

	public boolean getChangeEatAnimation() {
		return getTransformation() && config.getChangeFoodAnimation().get();
	}

	public float getEatMultiplier() {
		return (float) config.getEatMultiplier().fromPercent();
	}

	public boolean getTransformation() {
		return isEnabled() && config.getViewModel().get();
	}

	public float getOffsetX(Arm arm) {
		return (arm == Arm.LEFT ? -1f : 1f) * config.getEatX().getFloat();
	}

	public float getOffsetY() {
		return config.getEatY().getFloat();
	}

	public float getOffsetZ() {
		return config.getEatZ().getFloat();
	}

	public boolean getSwingChange() {
		return isEnabled() && config.getChangeSwingAnimation().get();
	}

	public boolean isShaderEnabled() {
		return isEnabled() && config.getShader().get();
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

	public MultiColor getOutlineColor() {
		if (config.getShaderType().get() == ShaderType.DEFAULT) {
			return config.getOutlineColor().get();
		}

		return config.getWaveColor().get();
	}

	public float getFillOpacity() {
		return (float) config.getFillOpacity().fromPercent();
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
		return config.getGradientSpeed().getFloat() / 10f;
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

	@Override
	public void onHeldItemRenderer(HeldItemRendererEvent event) {
		if (!config.getViewModel().get() || mc.player == null) {
			return;
		}

		if (event.getHand() == Hand.MAIN_HAND) {
			boolean isActive = mc.player.getActiveHand() == Hand.MAIN_HAND && mc.player.isUsingItem();

			if (!isActive) {
				rotate(
						event.getMatrices(),
						config.getRotateX().get(),
						config.getRotateY().get(),
						config.getRotateZ().get()
				);
			}
			scale(
					event.getMatrices(),
					config.getScale().fromPercent()
			);
			translate(
					event.getMatrices(),
					config.getX().get(),
					config.getY().get(),
					isActive ? 0 : config.getZ().get()
			);
		}
		else {
			boolean isActiveHand = mc.player.getActiveHand() == Hand.OFF_HAND && mc.player.isUsingItem();

			if (!isActiveHand) {
				rotate(
						event.getMatrices(),
						config.getRotateX().get(),
						-config.getRotateY().get(),
						-config.getRotateZ().get()
				);
			}
			scale(
					event.getMatrices(),
					config.getScale().fromPercent()
			);
			translate(
					event.getMatrices(),
					-config.getX().get(),
					config.getY().get(),
					isActiveHand ? 0 : config.getZ().get()
			);
		}
	}

	private void rotate(MatrixStack matrix, double x, double y, double z) {
		matrix.multiply(RotationAxis.POSITIVE_X.rotationDegrees((float) x));
		matrix.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float) y));
		matrix.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) z));
	}

	private void scale(MatrixStack matrix, double scale) {
		matrix.scale((float) scale, (float) scale, (float) scale);
	}

	private void translate(MatrixStack matrix, double x, double y, double z) {
		matrix.translate((float) x, (float) y, (float) z);
	}
}
