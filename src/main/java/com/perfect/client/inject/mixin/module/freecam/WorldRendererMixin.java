package com.perfect.client.inject.mixin.module.freecam;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.perfect.client.manager.system.FunctionManager.freecam;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

	@ModifyArg(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/render/WorldRenderer;setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V"
			),
			index = 3
	)
	private boolean perfect$renderSetupTerrainModifyArg(boolean spectator) {
		return freecam.isEnabled() || spectator;
	}
}
