package com.perfect.client.core.render.box;

import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.renderer.buffer.RenderBuffer;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static com.perfect.client.PerfectClient.mc;


public class BoxRenderer {

	public static void drawBuilder(VertexBuilder builder) {
		Renderer3D.preRender();
		Render.setShader(ShaderPrograms.POSITION_COLOR);
		builder.draw();
		Renderer3D.postRender();
	}

	public static void drawBox(
			MatrixStack matrices,
			Box box,
			MultiColor outlineColor,
			MultiColor fillColor,
			Cosmetics cosmetics
	) {
		if (!Renderer3D.isVisibleBox(matrices, box)) {
			return;
		}
	
		if (cosmetics.isShader()) {
			BoxShaderRenderer.drawShaderBox(
					matrices,
					box,
					outlineColor,
					fillColor,
					cosmetics.getShaderLineWidth(),
					cosmetics.isGradient(),
					cosmetics.getSegments(),
					cosmetics.getShaderGradientFactor(),
					cosmetics.getShaderGradientSpeed()
			);
		}
		else {
			drawBox(
					matrices,
					box,
					BoxColors.multiGradient(cosmetics, outlineColor, cosmetics.getSegments()),
					BoxColors.multiGradient(cosmetics, fillColor, cosmetics.getSegments()),
					cosmetics
			);
		}
	}

	public static void drawBox(
			MatrixStack matrices,
			Box box,
			BoxColors outlineColors,
			BoxColors fillColors,
			Cosmetics cosmetics
	) {
		if (!Renderer3D.isVisibleBox(matrices, box)) {
			return;
		}

		drawBox(
				Managers.RENDER.getGeneralBoxOutlineBuffer(),
				Managers.RENDER.getGeneralBoxFillBuffer(),
				matrices,
				box,
				cosmetics,
				outlineColors,
				fillColors
		);
	}

	public static void drawBoxImmediate(
			MatrixStack matrices,
			Box box,
			MultiColor outlineColor,
			MultiColor fillColor,
			Cosmetics cosmetics,
			Runnable GLArgs
	) {
		if (!Renderer3D.isVisibleBox(matrices, box)) {
			return;
		}

		drawBoxImmediate(
				matrices,
				box,
				BoxColors.multiGradient(cosmetics, outlineColor, cosmetics.getSegments()),
				BoxColors.multiGradient(cosmetics, fillColor, cosmetics.getSegments()),
				cosmetics,
				GLArgs
		);
	}

	public static void drawBoxImmediate(
			MatrixStack matrices,
			Box box,
			BoxColors outlineColors,
			BoxColors fillColors,
			Cosmetics cosmetics,
			Runnable GLArgs
	) {
		if (!Renderer3D.isVisibleBox(matrices, box)) {
			return;
		}

		Renderer3D.preRender();

		if (GLArgs != null) {
			GLArgs.run();
		}

		Render.setShader(ShaderPrograms.POSITION_COLOR);

		RenderBuffer outlineBuffer = Managers.RENDER.getImmediateBoxOutlineBuffer();
		RenderBuffer fillBuffer = Managers.RENDER.getImmediateBoxFillBuffer();

		drawBox(
				outlineBuffer,
				fillBuffer,
				matrices,
				box,
				cosmetics,
				outlineColors,
				fillColors
		);

		fillBuffer.draw(VertexBuilder::draw);
		outlineBuffer.draw(VertexBuilder::draw);

		Renderer3D.postRender();
	}

	public static void drawBox(
			RenderBuffer outlineBuffer,
			RenderBuffer fillBuffer,
			MatrixStack matrices,
			Box box,
			Cosmetics cosmetics,
			BoxColors outlineColors,
			BoxColors fillColors
	) {
		drawBoxFill(fillBuffer, matrices, box, fillColors);
		drawBoxOutline(outlineBuffer, matrices, box, cosmetics, outlineColors);
	}

	public static void drawBoxOutline(
			RenderBuffer buffer,
			MatrixStack matrices,
			Box box,
			Cosmetics cosmetics,
			BoxColors colors
	) {
		VertexBuilder builder = buffer.getVertexBuilder();
		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		float x1 = (float) (box.minX - camera.x);
		float y1 = (float) (box.minY - camera.y);
		float z1 = (float) (box.minZ - camera.z);
		float x2 = (float) (box.maxX - camera.x);
		float y2 = (float) (box.maxY - camera.y);
		float z2 = (float) (box.maxZ - camera.z);

		matrices.push();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);
		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);

		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);

		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);
		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);

		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);

		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);
		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);

		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);

		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);

		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);

		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);
		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);

		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);

		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);

		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);

		cosmetics.getPattern().render(builder, matrix, x1, y1, z1, x2, y2, z2, colors);
		matrices.pop();
	}

	public static void drawBoxFill(
			RenderBuffer buffer,
			MatrixStack matrices,
			Box box,
			MultiColor color,
			Cosmetics cosmetics
	) {
		drawBoxFill(buffer, matrices, box, BoxColors.gradient(cosmetics, color));
	}

	public static void drawBoxFill(
			RenderBuffer buffer,
			MatrixStack matrices,
			Box box,
			BoxColors colors
	) {
		VertexBuilder builder = buffer.getVertexBuilder();
		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		float x1 = (float) (box.minX - camera.x);
		float y1 = (float) (box.minY - camera.y);
		float z1 = (float) (box.minZ - camera.z);
		float x2 = (float) (box.maxX - camera.x);
		float y2 = (float) (box.maxY - camera.y);
		float z2 = (float) (box.maxZ - camera.z);

		matrices.push();
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);
		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);

		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);

		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);
		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);

		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);

		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);
		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);
		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);

		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);
		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);

		matrices.pop();
	}
}