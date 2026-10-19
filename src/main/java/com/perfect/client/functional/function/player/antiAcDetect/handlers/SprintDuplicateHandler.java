package com.perfect.client.functional.function.player.antiAcDetect.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;

// Клиент не может отправлять несколько ClientCommandC2SPacket с одинаковым статусом SPRINTING.
// Грим детектит как BadPacketsF.


public class SprintDuplicateHandler extends AntiAcDetectHandler {

	public SprintDuplicateHandler(
			AntiAcDetect function,
			AntiAcDetectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onAntiAcDetectPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
			if (isDuplicate(packet)) {
				event.cancel();
			}
		}
	}

	protected boolean isDuplicate(ClientCommandC2SPacket packet) {
		return switch (packet.getMode()) {
			case START_SPRINTING -> Managers.NETWORK.isSprinting();
			case STOP_SPRINTING -> !Managers.NETWORK.isSprinting();
			default -> false;
		};
	}
}
