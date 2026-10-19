package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.block.ServerBlockUpdateEvent;
import com.perfect.client.core.event.events.entity.EntityEvent;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

	@Inject(
			method = "addEntity",
			at = @At("TAIL")
	)
	public void perfect$addEntityHookPost(Entity entity, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onEntitySpawn(new EntityEvent(entity));
	}

	@Inject(
			method = "tickEntities",
			at = @At("TAIL")
	)
	public void perfect$tickEntities(CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onTickEntities();
	}

	@Inject(
			method = "handleBlockUpdate",
			at = @At(value = "TAIL")
	)
	public void perfect$handleBlockUpdate(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onServerBlockUpdate(new ServerBlockUpdateEvent(pos, state));
	}
}
