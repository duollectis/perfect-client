package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.network.ScreenHandlerSlotUpdateEvent;
import com.perfect.client.core.event.events.world.ChunkDataEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

	@Shadow
	private ClientWorld world;

	@Inject(
			method = "onGameJoin",
			at = @At("TAIL")
	)
	private void perfect$onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
		if (world != null) {
			PerfectClient.EVENT_MANAGER.onGameLeft();
		}

		PerfectClient.EVENT_MANAGER.onGameJoin();
	}

	@Inject(
			method = "onScreenHandlerSlotUpdate",
			at = @At("RETURN")
	)
	public void perfect$onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket p, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onScreenHandlerSlotUpdate(new ScreenHandlerSlotUpdateEvent(
				p.getSyncId(),
				p.getRevision(),
				p.getSlot(),
				p.getStack()
		));
	}

	@Inject(
			method = "onPlayerRespawn",
			at = @At("TAIL")
	)
	public void perfect$onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onRespawn();
	}

	@Inject(
			method = "onChunkData",
			at = @At("TAIL")
	)
	private void onChunkData(ChunkDataS2CPacket packet, CallbackInfo info) {
		if (mc.world == null) {
			return;
		}

		Chunk chunk = mc.world.getChunk(packet.getChunkX(), packet.getChunkZ());
		PerfectClient.EVENT_MANAGER.onChunkData(new ChunkDataEvent(chunk));
	}
}
