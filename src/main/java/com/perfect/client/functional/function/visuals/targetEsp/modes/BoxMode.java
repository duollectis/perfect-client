package com.perfect.client.functional.function.visuals.targetEsp.modes;

import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.visuals.targetEsp.TargetESP;
import com.perfect.client.functional.function.visuals.targetEsp.TargetEspConfig;
import com.perfect.client.functional.function.visuals.targetEsp.utils.RenderHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class BoxMode extends RenderHandler {

	private final Animation visibleAnimation = new Animation(Easing.IN_OUT_CUBIC, 300).finish(false);

	public BoxMode(TargetESP function, TargetEspConfig config) {
		super(function, config);
	}

	@Override
	public void reset() {
		visibleAnimation.finish(false);
	}

	@Override
	public void setVisible(boolean visible) {
		visibleAnimation.changeDirection(visible);
	}

	@Override
	public void render(MatrixStack matrices, LivingEntity entity) {
		if (visibleAnimation.isFinished(false)) {
			return;
		}

		Vec3d pos = RenderUtils.getEntityPos(entity);
		Box box = EntityUtils.getBoxAt(entity, pos);

		float animation = visibleAnimation.get();

		BoxRenderer.drawBoxImmediate(
				matrices,
				box,
				config.getBoxLineColor().get().opacity(animation),
				config.getBoxSideColor().get().opacity(animation),
				config.getBoxCosmetics().get().bundle(),
				() -> {
					if (!config.getThroughObstacles().get()) {
						Render.enableDepthTest();
					}
				}
		);
	}
}
