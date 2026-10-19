package com.perfect.client.functional.function.player.antiAcDetect.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

import java.util.ArrayDeque;
import java.util.Queue;


public class MultiSwapHandler extends AntiAcDetectHandler {

	private final Queue<Integer> swapQueue = new ArrayDeque<>();
	private boolean swappedThisTick;

	public MultiSwapHandler(
			AntiAcDetect function,
			AntiAcDetectConfig config
	) {
		super(function, config);
	}

	@Override
	public void onDisable() {
		swapQueue.clear();
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket) {
			swappedThisTick = true;
		}
	}

	
	@Override
	public void onAntiAcDetectPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
			if (swappedThisTick) {
				swapQueue.add(packet.getSelectedSlot());
				event.cancel();
			}
		}
	}

	
	@Override
	public void onPreTick() {
		swappedThisTick = false;

		Integer slot = swapQueue.poll();

		if (slot != null) {
			InvUtils.setSlot(slot, false);
		}
	}
}
