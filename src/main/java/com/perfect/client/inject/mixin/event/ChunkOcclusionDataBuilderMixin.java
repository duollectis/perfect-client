package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.Cancellable;
import net.minecraft.client.render.chunk.ChunkOcclusionDataBuilder;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkOcclusionDataBuilder.class)
public class ChunkOcclusionDataBuilderMixin {

	@Inject(
			method = "markClosed",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onMarkClosed(BlockPos pos, CallbackInfo info) {
		Cancellable event = new Cancellable();
		PerfectClient.EVENT_MANAGER.onChunkOcclusion(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}
}
