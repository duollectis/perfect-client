package com.perfect.client.core.render.renderer;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;

public class ModelRenderer {
	private static final MatrixStack cleanEntityMatrices = new MatrixStack();
	private static MatrixStack eventMatrices;
	public static boolean isRenderingOutline = false;

	public static void render(Render3DEvent event, float scale, Entity entity, Color lineColor, Color sideColor) {
		ModelRenderer.eventMatrices = event.getMatrices();

		if (!Renderer3D.isVisibleBox(ModelRenderer.eventMatrices, entity.getBoundingBox())) {
			return;
		}

		VertexBuilder lineBuilder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.LINES);
		VertexBuilder quadBuilder = new VertexBuilder(VertexFormats.POSITION_COLOR, VertexDrawMode.QUADS);
		Vec3d entityWorldPos = entity.getLerpedPos(event.getTickDelta());
		Vec3d camera = mc.gameRenderer.getCamera().getPos();
		Vec3d pivotWorld = entity.getBoundingBox().getCenter();

		MyVertexConsumerProvider provider = new MyVertexConsumerProvider(
				lineBuilder,
				quadBuilder,
				lineColor,
				sideColor,
				camera,
				entityWorldPos,
				pivotWorld,
				scale
		);

		@SuppressWarnings("unchecked")
		EntityRenderer<Entity, EntityRenderState> renderer =
				(EntityRenderer<Entity, EntityRenderState>) mc.getEntityRenderDispatcher().getRenderer(entity);

		EntityRenderState state = renderer.getAndUpdateRenderState(entity, event.getTickDelta());

		cleanEntityMatrices.push();

		Renderer3D.preRender();
		Render.setShader(ShaderPrograms.POSITION_COLOR);

		eventMatrices.push();

		isRenderingOutline = true;
		renderer.render(state, cleanEntityMatrices, provider, 15);
		isRenderingOutline = false;

		quadBuilder.draw();
		lineBuilder.draw();

		eventMatrices.pop();
		Renderer3D.postRender();

		cleanEntityMatrices.pop();
	}

	public static class MyVertexConsumerProvider implements VertexConsumerProvider {
		private final MyVertexConsumer consumer;

		public MyVertexConsumerProvider(
				VertexBuilder lines,
				VertexBuilder quads,
				Color line,
				Color side,
				Vec3d camera,
				Vec3d entityWorldPos,
				Vec3d pivotWorld,
				float scale
		) {
			this.consumer = new MyVertexConsumer(lines, quads, line, side, camera, entityWorldPos, pivotWorld, scale);
		}

		@Override
		public VertexConsumer getBuffer(RenderLayer layer) {
			if (layer instanceof RenderLayer.MultiPhase phase && phase.phases.target == RenderLayer.ITEM_ENTITY_TARGET) {
				return NoopVertexConsumer.INSTANCE;
			}
			return consumer;
		}
	}

	public static class MyVertexConsumer implements VertexConsumer {
		private final VertexBuilder lineBuilder;
		private final VertexBuilder quadBuilder;
		private final Color lineColor;
		private final Color sideColor;
		private final Vec3d camera;
		private final Vec3d entityWorldPos;
		private final Vec3d pivotWorld;
		private final float scale;

		private final float[] xs = new float[4];
		private final float[] ys = new float[4];
		private final float[] zs = new float[4];
		private int i = 0;

		public MyVertexConsumer(
				VertexBuilder lines,
				VertexBuilder quads,
				Color line,
				Color side,
				Vec3d camera,
				Vec3d entityWorldPos,
				Vec3d pivotWorld,
				float scale
		) {
			this.lineBuilder = lines;
			this.quadBuilder = quads;
			this.lineColor = line;
			this.sideColor = side;
			this.camera = camera;
			this.entityWorldPos = entityWorldPos;
			this.pivotWorld = pivotWorld;
			this.scale = scale;
		}

		@Override
		public VertexConsumer vertex(float x, float y, float z) {
			// Сначала переводим вершину в мировые координаты.
			double worldX = x + entityWorldPos.getX();
			double worldY = y + entityWorldPos.getY();
			double worldZ = z + entityWorldPos.getZ();

			// Затем масштабируем вокруг центра модели.
			double scaledWorldX = pivotWorld.getX() + (worldX - pivotWorld.getX()) * scale;
			double scaledWorldY = pivotWorld.getY() + (worldY - pivotWorld.getY()) * scale;
			double scaledWorldZ = pivotWorld.getZ() + (worldZ - pivotWorld.getZ()) * scale;

			// И уже потом переводим обратно в координаты относительно камеры.
			xs[i] = (float) (scaledWorldX - camera.x);
			ys[i] = (float) (scaledWorldY - camera.y);
			zs[i] = (float) (scaledWorldZ - camera.z);

			i++;

			if (i == 4) {
				for (int j = 0; j < 4; j++) {
					int next = (j + 1) % 4;

					float x1 = xs[j];
					float y1 = ys[j];
					float z1 = zs[j];

					float x2 = xs[next];
					float y2 = ys[next];
					float z2 = zs[next];

					lineBuilder.pos(eventMatrices, x1, y1, z1).color(lineColor);
					lineBuilder.pos(eventMatrices, x2, y2, z2).color(lineColor);

					quadBuilder.pos(eventMatrices, x1, y1, z1).color(sideColor);
				}
				i = 0;
			}

			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			return this;
		}
	}

	public static class NoopVertexConsumer implements VertexConsumer {
		public static final NoopVertexConsumer INSTANCE = new NoopVertexConsumer();

		@Override
		public VertexConsumer vertex(float x, float y, float z) {
			return this;
		}

		@Override
		public VertexConsumer color(int red, int green, int blue, int alpha) {
			return this;
		}

		@Override
		public VertexConsumer texture(float u, float v) {
			return this;
		}

		@Override
		public VertexConsumer overlay(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer light(int u, int v) {
			return this;
		}

		@Override
		public VertexConsumer normal(float x, float y, float z) {
			return this;
		}
	}
}