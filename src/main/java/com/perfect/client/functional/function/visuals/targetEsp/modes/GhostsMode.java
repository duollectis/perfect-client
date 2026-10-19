package com.perfect.client.functional.function.visuals.targetEsp.modes;

import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.visuals.targetEsp.TargetESP;
import com.perfect.client.functional.function.visuals.targetEsp.TargetEspConfig;
import com.perfect.client.functional.function.visuals.targetEsp.utils.RenderHandler;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class GhostsMode extends RenderHandler {

	private static final float SIZE = 0.17f;
	private static final float RANGE = 0.25f;
	private static final int POINTS = 360;
	private static final int LENGTH = 120;

	private final Animation visibleAnimation = new Animation(Easing.IN_OUT_SINE, 450).finish(false);

	private float prevAnimation;
	private float animation;

	public GhostsMode(TargetESP function, TargetEspConfig config) {
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
		prevAnimation = animation;
		animation += MathUtils.interpolateFloat(10.0f, 35.0f, config.getGhostsSpeed().fromPercent());

		if (animation >= 360.0f && prevAnimation >= 360.0f) {
			animation = animation % 360.0f;
			prevAnimation = prevAnimation % 360.0f;
		}
	}

	@Override
	public void render(MatrixStack matrices, LivingEntity entity) {
		if (visibleAnimation.isFinished(false)) {
			return;
		}

		float anim = MathUtils.interpolateFloat(prevAnimation, animation, RenderUtils.getTickDelta());
		float visible = visibleAnimation.get();

		Vec3d pos = RenderUtils.getEntityPos(entity);

		float x = (float) pos.getX();
		float y = (float) pos.getY() + entity.getHeight() / 2.0f;
		float z = (float) pos.getZ();

		float targetWidth = entity.getWidth() + RANGE;

		Renderer3D.preRender();

		Renderer3D.disableSmooth();

		if (!config.getThroughObstacles().get()) {
			Render.enableDepthTest();
		}

		Render.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);

		Render.setShader(ShaderPrograms.POSITION_TEXTURE_COLOR);
		Render.setTexture(Textures.FIREFLY);

		VertexBuilder builder = new VertexBuilder(
				VertexFormats.POSITION_TEXTURE_COLOR,
				VertexDrawMode.QUADS
		);

		Color color = config.getColor().get().getColor();
		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		matrices.push();
		matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());

		float length = LENGTH * visible;

		for (int ghost = 0; ghost < 3; ghost++) {
			for (int point = 0; point < (int) length; point++) {
				float angle = (float) (point * (2 * Math.PI / POINTS));

				Quaternionf rotation = new Quaternionf()
						.rotateY((float) Math.toRadians(120.0f * ghost))
						.rotateX((float) Math.toRadians(45.0f))
						.rotateY((float) Math.toRadians(120.0f * ghost + anim));

				float cos = targetWidth * (float) Math.cos(angle);
				float sin = targetWidth * (float) Math.sin(angle);

				Vector3f rotated = rotation.transform(cos, 0, sin, new Vector3f());

				float px = x + rotated.x;
				float py = y + rotated.y;
				float pz = z + rotated.z;

				float sizeFactor = (1.0f - ((float) point / length)) * visible;

				Color4f col = new Color4f(ColorUtils.multiplyAlpha(color, sizeFactor));
				float size = SIZE * sizeFactor;

				matrices.push();

				matrices.translate(px, py, pz);
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
				matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));

				Matrix4f matrix = matrices.peek().getPositionMatrix();

				builder.pos(matrix, -size, size, 0.0f).tex(0.0f, 1.0f).color(col);
				builder.pos(matrix, size, size, 0.0f).tex(1.0f, 1.0f).color(col);
				builder.pos(matrix, size, -size, 0.0f).tex(1.0f, 0.0f).color(col);
				builder.pos(matrix, -size, -size, 0.0f).tex(0.0f, 0.0f).color(col);

				matrices.pop();
			}
		}

		matrices.pop();
		builder.draw();
		Renderer3D.postRender();
	}
}
