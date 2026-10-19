package com.perfect.client.inject.mixin.module.chams;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.chams;

@Mixin(EndCrystalEntityModel.class)
public abstract class EndCrystalEntityModelMixin {

	@ModifyExpressionValue(
			method = "setAngles(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/entity/EndCrystalEntityRenderer;getYOffset(F)F"
			)
	)
	private float perfect$modifyCrystalsBounce(
			float original,
			EndCrystalEntityRenderState state
	) {
		if (!chams.getCrystals()) {
			return original;
		}

		float g = MathHelper.sin(state.age * 0.2f * (float) chams.getCrystalBounceSpeed()) / 2.0F + 0.5F;
		g = (g * g + g) * 0.4F * (float) chams.getCrystalBounce();
		return g - 1.4F;
	}

	@ModifyExpressionValue(
			method = "setAngles(Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;)V",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/client/render/entity/state/EndCrystalEntityRenderState;age:F",
					ordinal = 0
			)
	)
	private float perfect$modifyRotationSpeed(float original) {
		if (!chams.getCrystals()) {
			return original;
		}

		return original * (float) chams.getCrystalsRotationSpeed();
	}
}