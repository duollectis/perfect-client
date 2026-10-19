package com.perfect.client.inject.mixin.postProcessManager;

import com.perfect.client.core.render.postprocess.PostProcessManager;
import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.nametags;
import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

	@Inject(
			method = "renderLabelIfPresent",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderLabel(
			S state,
			Text text,
			MatrixStack matrices,
			VertexConsumerProvider vertexConsumers,
			int light,
			CallbackInfo ci
	) {
		if (PostProcessManager.rendering) {
			ci.cancel();
		}
		if (noRender.contains(Elements.NAMETAGS)) {
			ci.cancel();
		}
		if (nametags.isEnabled() && nametags.shouldDraw(state.entityType)) {
			ci.cancel();
		}
	}
}
