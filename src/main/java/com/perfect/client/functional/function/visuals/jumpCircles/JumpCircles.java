package com.perfect.client.functional.function.visuals.jumpCircles;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JumpCircles extends Function {

	private final JumpCirclesConfig config = new JumpCirclesConfig(this);

	private List<Circle> circles;

	public JumpCircles() {
		super("jump-circles", "Jump Circles", Category.VISUALS);
	}

	@Override
	public void onEnable() {
		circles = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		circles = null;
	}

	@Override
	public void onPreJump(Cancellable event) {
		if (mc.player != null && mc.player.isOnGround()) {
			circles.add(new Circle(mc.player.getPos().add(0, 0.01, 0)));
		}
	}

	@Override
	public void onPostTick() {
		circles.removeIf(circle -> MathUtils.isNear(circle.colorAnimation.getCurrent(), 0));
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		List<Circle> circles = new ArrayList<>(this.circles);

		Renderer3D.preRender();

		Render.enableDepthTest();
		Render.enableBlend();
		Render.depthMask(false);

		Render.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);

		Render.setTexture(Textures.CIRCLE);
		Render.setShader(ShaderPrograms.BRIGHT_POSITION_TEXTURE_COLOR);

		VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION_TEXTURE_COLOR, VertexDrawMode.QUADS);

		MatrixStack matrices = event.getMatrices();

		Color color1 = config.getColor().get().getColor();
		Color color2 = config.getColor().get().getOffsetColor(0.25f);
		Color color3 = config.getColor().get().getOffsetColor(0.5f);
		Color color4 = config.getColor().get().getOffsetColor(0.75f);

		for (Circle circle : circles) {
			float size = (float) config.getScale().fromPercent() * circle.sizeAnimation.updateAndGet();
			float alpha = circle.colorAnimation.updateAndGet();

			matrices.push();
			matrices.translate(
					circle.pos.getX() - mc.getEntityRenderDispatcher().camera.getPos().getX(),
					circle.pos.getY() - mc.getEntityRenderDispatcher().camera.getPos().getY(),
					circle.pos.getZ() - mc.getEntityRenderDispatcher().camera.getPos().getZ()
			);

			boolean higher = mc.gameRenderer.getCamera().getPos().getY() > circle.pos.getY();

			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(higher ? 90 : -90));
			Matrix4f matrix = matrices.peek().getPositionMatrix();

			builder.pos(matrix, -size, size, 0)
			       .tex(0, 1)
			       .color(ColorUtils.multiplyAlpha(color1, alpha).getRGB());

			builder.pos(matrix, size, size, 0)
			       .tex(1, 1)
			       .color(ColorUtils.multiplyAlpha(color2, alpha).getRGB());

			builder.pos(matrix, size, -size, 0)
			       .tex(1, 0)
			       .color(ColorUtils.multiplyAlpha(color3, alpha).getRGB());

			builder.pos(matrix, -size, -size, 0)
			       .tex(0, 0)
			       .color(ColorUtils.multiplyAlpha(color4, alpha).getRGB());

			matrices.pop();
		}

		builder.draw();

		Renderer3D.postRender();
	}

	public static class Circle {

		private final FloatAnimation sizeAnimation = new FloatAnimation(0.015).current(0.0f).target(1.0f);
		private final FloatAnimation colorAnimation = new FloatAnimation(0.015).current(1.0f).target(0.0f);

		private final Vec3d pos;

		public Circle(Vec3d pos) {
			this.pos = pos;
		}
	}
}
