package com.perfect.client.inject.mixin.module.chinaHat;

import com.perfect.client.functional.function.visuals.chinaHat.utils.ChinaHatFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {

	public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}
	@Inject(
			method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V",
			at = @At("TAIL")
	)
	private void perfect$init(EntityRendererFactory.Context context, boolean bl, CallbackInfo ci) {
		addFeature(new ChinaHatFeatureRenderer<>(this));
	}
}
