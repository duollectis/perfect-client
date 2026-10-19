package com.perfect.client.functional.function.visuals.targetEsp.modes;

import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.visuals.targetEsp.TargetESP;
import com.perfect.client.functional.function.visuals.targetEsp.TargetEspConfig;
import com.perfect.client.functional.function.visuals.targetEsp.utils.RenderHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

public class CircleMode extends RenderHandler {

	private static final float RADIANS_PER_STEP = (float) (Math.TAU / 180.0);

	private final Animation visibleAnimation = new Animation(Easing.IN_OUT_CUBIC, 400).finish(false);

	private float prevCircleY;
	private float circleY;

	public CircleMode(TargetESP function, TargetEspConfig config) {
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
	public void onPreTick() {
		prevCircleY = circleY;
		circleY += 0.15f;
	}

	@Override
	public void render(MatrixStack matrices, LivingEntity entity) {
		if (visibleAnimation.isFinished(false)) {
			return;
		}

		float circleRenderY = MathUtils.interpolateFloat(prevCircleY, circleY, RenderUtils.getTickDelta());

		float prevSinAnim = absSinAnimation(circleRenderY - 0.45f);
		float sinAnim = absSinAnimation(circleRenderY);

		Vec3d camera = mc.gameRenderer.getCamera().getPos();
		Vec3d pos = RenderUtils.getEntityPos(entity);

		matrices.push();
		matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

		float x = (float) pos.getX();
		float y = (float) pos.getY();
		float z = (float) pos.getZ();

		float y1 = y + prevSinAnim * entity.getHeight();
		float y2 = y + sinAnim * entity.getHeight();

		float targetWidth = entity.getWidth();
		float opacity = visibleAnimation.get();

		Renderer3D.preRender();

		if (!config.getThroughObstacles().get()) {
			Render.enableDepthTest();
		}

		Render.setShader(ShaderPrograms.POSITION_COLOR);

		VertexBuilder lineBuilder = new VertexBuilder(
				VertexFormats.POSITION_COLOR,
				VertexDrawMode.LINE_STRIP
		);

		VertexBuilder sideBuilder = new VertexBuilder(
				VertexFormats.POSITION_COLOR,
				VertexDrawMode.TRIANGLE_STRIP
		);


		Color4f sideColor = new Color4f(
				ColorUtils.multiplyAlpha(config.getCircleTrailColor().get().getColor(), opacity)
		);

		Color4f transparentSideColor = sideColor.withAlpha(0.0f);

		Color4f lineColor = new Color4f(
				ColorUtils.multiplyAlpha(config.getCircleLineColor().get().getColor(), opacity)
		);

		for (int i = 0; i <= 180; i++) {
			float cos = (float) (x + Math.cos(i * RADIANS_PER_STEP) * targetWidth);
			float sin = (float) (z + Math.sin(i * RADIANS_PER_STEP) * targetWidth);

			sideBuilder.pos(matrices, cos, y2, sin).color(sideColor);
			sideBuilder.pos(matrices, cos, y1, sin).color(transparentSideColor);

			lineBuilder.pos(matrices, cos, y2, sin).color(lineColor);
		}

		Renderer3D.disableSmooth();
		sideBuilder.draw();

		Renderer3D.enableSmooth();
		lineBuilder.draw();

		Renderer3D.postRender();

		matrices.pop();
	}

	public float absSinAnimation(float input) {
		return Math.abs(1.0f + (float) Math.sin(input)) / 2.0f;
	}
}
