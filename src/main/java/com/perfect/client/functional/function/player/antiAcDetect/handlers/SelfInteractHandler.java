package com.perfect.client.functional.function.player.antiAcDetect.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;
import com.perfect.client.inject.mixinInterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

// Клиент не может взаимодействовать с клиент плеером.
// BadPacketsC в гриме.


public class SelfInteractHandler extends AntiAcDetectHandler {

	public SelfInteractHandler(
			AntiAcDetect function,
			AntiAcDetectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onAntiAcDetectPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
			onPlayerInteractEntityPacket(event, packet);
		}
	}

	
	private void onPlayerInteractEntityPacket(Cancellable event, PlayerInteractEntityC2SPacket p) {
		if(mc.player == null) {
			return;
		}

		IPlayerInteractEntityC2SPacket packet = (IPlayerInteractEntityC2SPacket) p;

		if (mc.player.equals(packet.perfect$getEntity())) {
			event.cancel();
		}
	}
}
