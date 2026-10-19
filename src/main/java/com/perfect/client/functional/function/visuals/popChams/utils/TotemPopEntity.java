package com.perfect.client.functional.function.visuals.popChams.utils;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.render.renderer.ModelRenderer;
import com.perfect.client.core.utils.game.entity.CopyPositionEntity;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.visuals.popChams.PopChamsConfig;
import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.entity.player.PlayerEntity;

public class TotemPopEntity extends CopyPositionEntity {

	private final PopChamsConfig config;
	private final TimerUtils timer = new TimerUtils();
	private final double startY;
	private final double endY;

	private float scale = 1;

	public TotemPopEntity(PopChamsConfig config, PlayerEntity player) {
		super(player, "pop-chams-entity");
		this.config = config;

		uuid = player.getUuid();
		startY = getY();
		endY = startY + config.getChangeY().get();
	}

	public boolean updateAndRender(Render3DEvent event) {
		if (timer.passedSeconds(config.getRenderTime().get())) {
			return true;
		}

		applyVerticalMovement();
		applyScaleAnimation();
		applyRotation();

		float opacityMultiplier = calculateOpacityMultiplier();
		ModelRenderer.render(
				event,
				scale,
				this,
				config.getLineColor().get().getOpacityColor(opacityMultiplier),
				config.getFillColor().get().getOpacityColor(opacityMultiplier)
		);

		return false;
	}

	private void applyVerticalMovement() {
		if (config.getChangeY().isEnabled()) {
			double currentY = MathUtils.interpolate(startY, endY, getInterpolationFactor());
			updatePositionY(currentY);
		}
	}

	private void applyScaleAnimation() {
		if (config.getChangeScale().isEnabled()) {
			scale = (float) MathUtils.interpolate(
					1,
					config.getChangeScale().get(),
					getInterpolationFactor()
			);
		}
	}

	private void applyRotation() {
		if (config.getChangeRotate().isEnabled()) {
			float rotationOffset = config.getChangeRotate().getFloat();

			setYaw(getYaw() + rotationOffset);

			serverHeadYaw = lastYaw = lastHeadYaw = headYaw += rotationOffset;
			lastBodyYaw = bodyYaw += rotationOffset;
		}
	}

	private float calculateOpacityMultiplier() {
		if (!config.getFadeOut().get()) {
			return 1f;
		}

		return (float) (1.0 - getInterpolationFactor());
	}

	private double getInterpolationFactor() {
		return timer.getSeconds() / config.getRenderTime().get();
	}

	private void updatePositionY(double y) {
		((IVec3d) getPos()).perfect$setY(y);
		lastY = lastRenderY = y;
	}
}