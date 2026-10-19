package com.perfect.client.functional.function.misc.autoOminous.handlers;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.render.animation.value.ColorAnimation;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.functional.function.misc.autoOminous.AutoOminous;
import com.perfect.client.functional.function.misc.autoOminous.AutoOminousConfig;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;

public class RenderHandler extends FunctionHandler<AutoOminous, AutoOminousConfig> {

	private static final int POINTS = 512;
	private static final double HALF_POINTS = POINTS / 2.0;

	private final ColorAnimation colorAnimation = new ColorAnimation().defined(false).speed(0.03);

	public RenderHandler(AutoOminous function, AutoOminousConfig config) {
		super(function, config);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (!config.getRender().get() || mc.player == null) {
			return;
		}

		BlockPos pos = function.getFarmPos();

		if (pos == null) {
			return;
		}

		float range = config.getTriggerRange().getFloat();

		Renderer3D.preRender();

		Render.setColor(colorAnimation.updateAndGet(getColor()));

		Render.setShader(ShaderPrograms.POSITION);
		VertexBuilder builder = new VertexBuilder(VertexFormats.POSITION, VertexDrawMode.LINE_STRIP);

		MatrixStack matrices = event.getMatrices();

		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		matrices.push();
		matrices.translate(-camera.getX(), -camera.getY(), -camera.getZ());
		matrices.translate(pos.getX(), pos.getY(), pos.getZ());

		Matrix4f matrix = matrices.peek().getPositionMatrix();

		for (int i = 0; i <= POINTS; i++) {
			double rad = i * Math.PI / HALF_POINTS;
			float x = (float) Math.cos(rad) * range;
			float z = (float) Math.sin(rad) * range;

			builder.pos(matrix, x + 0.5f, 0.0f, z + 0.5f);
		}

		matrices.pop();

		builder.draw();
		Renderer3D.postRender();
	}

	private Color getColor() {
		if (function.isOutOfRange()) {
			return config.getInactiveColor().getColor();
		}

		if (function.isUsing() || function.isFound() || function.checkEffects()) {
			return config.getActiveColor().getColor();
		}

		return config.getInactiveColor().getColor();
	}
}
