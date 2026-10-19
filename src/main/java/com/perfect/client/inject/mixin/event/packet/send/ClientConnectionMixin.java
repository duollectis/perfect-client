package com.perfect.client.inject.mixin.event.packet.send;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.network.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(
			method = "send(Lnet/minecraft/network/packet/Packet;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onSendPacketHead(Packet<?> packet, CallbackInfo ci) {
		PacketEvent event = new PacketEvent(packet);

		PerfectClient.EVENT_MANAGER.onPrePacketSend(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "send(Lnet/minecraft/network/packet/Packet;)V",
			at = @At("TAIL")
	)
	private void perfect$onSendPacketTail(Packet<?> packet, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onPostPacketSend(new PacketEvent(packet));
	}
}
