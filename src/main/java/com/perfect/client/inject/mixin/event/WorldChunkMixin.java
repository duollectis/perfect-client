package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldChunk.class)
public class WorldChunkMixin {

	@Shadow
	@Final
	World world;

	@Inject(
			method = "setBlockState",
			at = @At("TAIL")
	)
	private void perfect$onSetBlockState(
			BlockPos pos,
			BlockState state,
			int flags,
			CallbackInfoReturnable<BlockState> cir
	) {
		if (world.isClient) {
			PerfectClient.EVENT_MANAGER.onBlockUpdate(new BlockUpdateEvent(pos, cir.getReturnValue(), state));
		}
	}
}
