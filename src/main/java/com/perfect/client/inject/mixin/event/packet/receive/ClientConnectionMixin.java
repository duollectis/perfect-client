package com.perfect.client.inject.mixin.event.packet.receive;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.network.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.function.Consumer;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

	@Inject(
			method = "handlePacket",
			at = @At("HEAD"),
			cancellable = true
	)
	private static <T extends PacketListener> void perfect$beforePacketHandled(
			Packet<T> packet,
			PacketListener listener,
			CallbackInfo ci
	) {
		if (perfect$post(packet, PerfectClient.EVENT_MANAGER::onPrePacketReceive)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "handlePacket",
			at = @At("HEAD")
	)
	private static <T extends PacketListener> void perfect$afterPacketHandled(
			Packet<T> packet,
			PacketListener listener,
			CallbackInfo ci
	) {
		perfect$post(packet, PerfectClient.EVENT_MANAGER::onPostPacketReceive);
	}

	@Unique
	private static boolean perfect$post(Packet<?> packet, Consumer<PacketEvent> eventConsumer) {
		if (packet instanceof BundleS2CPacket bundle) {
			for (Iterator<Packet<? super ClientPlayPacketListener>> it = bundle.getPackets().iterator();
			     it.hasNext(); ) {

				PacketEvent event = new PacketEvent(it.next());
				eventConsumer.accept(event);

				if (event.isCancelled()) {
					it.remove();
				}
			}

			return false;
		}

		PacketEvent event = new PacketEvent(packet);
		eventConsumer.accept(event);

		return event.isCancelled();
	}
}
