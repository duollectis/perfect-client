package com.perfect.client.inject.mixin.module.entityESP;

import com.perfect.client.core.render.postprocess.PostProcessManager;
import com.perfect.client.core.render.postprocess.processors.EntityProcessor;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.inject.mixinInterface.IWorldRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@Shadow
	@Final
	private EntityRenderDispatcher entityRenderDispatcher;


	@Inject(
			method = "renderEntity",
			at = @At("HEAD")
	)
	private void perfect$renderEntity(
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			float tickDelta,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			CallbackInfo info
	) {
		perfect$draw(
				entity,
				cameraX,
				cameraY,
				cameraZ,
				tickDelta,
				vertexConsumers,
				matrices,
				PostProcessManager.ENTITY_OUTLINE_PROCESSOR,
				RenderUtils.getEntityColor(entity, Color.BLACK)
		);

		perfect$draw(
				entity,
				cameraX,
				cameraY,
				cameraZ,
				tickDelta,
				vertexConsumers,
				matrices,
				PostProcessManager.CHAMS_PROCESSOR,
				RenderUtils.getEntityColor(entity, Color.WHITE)
		);
	}

	@Unique
	private void perfect$draw(
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			float tickDelta,
			VertexConsumerProvider vertexConsumers,
			MatrixStack matrices,
			EntityProcessor shader,
			Color color
	) {
		if (shader.shouldDraw(entity) && !PostProcessManager.isCustom(vertexConsumers) && color != null) {
			((IWorldRenderer) this).perfect$pushEntityOutlineFramebuffer(shader.getFramebuffer());

			PostProcessManager.rendering = true;

			shader.getVertexConsumerProvider().setColor(
					color.getRed(),
					color.getGreen(),
					color.getBlue(),
					color.getAlpha()
			);

			perfect$renderEntityIternal(
					entity,
					cameraX, cameraY, cameraZ,
					tickDelta,
					shader.getVertexConsumerProvider(),
					matrices
			);

			PostProcessManager.rendering = false;
			((IWorldRenderer) this).perfect$popEntityOutlineFramebuffer();
		}
	}

	@Unique
	private void perfect$renderEntityIternal(
			Entity entity,
			double cameraX,
			double cameraY,
			double cameraZ,
			float tickDelta,
			VertexConsumerProvider vertexConsumers,
			MatrixStack matrices
	) {
		double d = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
		double e = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
		double f = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());

		entityRenderDispatcher.render(
				entity,
				d - cameraX,
				e - cameraY,
				f - cameraZ,
				tickDelta,
				matrices,
				vertexConsumers,
				this.entityRenderDispatcher.getLight(entity, tickDelta)
		);
	}
}
