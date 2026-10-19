package com.perfect.client.functional.function.player.antiAcDetect.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

// Ванильный клиент не отправляет несколько UpdateSelectedSlotC2SPacket с одинаковым slot.
// Обратный случай грим детектит как BadPacketsA.


public class SlotDuplicateHandler extends AntiAcDetectHandler {

	public SlotDuplicateHandler(
			AntiAcDetect function,
			AntiAcDetectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onAntiAcDetectPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
			onUpdateSlotPacket(event, packet);
		}
	}

	public void onUpdateSlotPacket(Cancellable event, UpdateSelectedSlotC2SPacket packet) {
		if (Managers.NETWORK.isHolding(packet.getSelectedSlot())) {
			event.cancel();
		}
	}
}
