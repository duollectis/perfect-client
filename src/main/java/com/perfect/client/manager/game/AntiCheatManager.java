package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

import java.util.concurrent.atomic.AtomicInteger;

public class AntiCheatManager extends Manager {

	private static final int CC_CRYSTAL_SWAP_DELAY = 9;

	private final AtomicInteger ticksSinceCrystalSwap = new AtomicInteger();
	private final TimerUtils setBackTimer = new TimerUtils();

	@Override
	public void onPreTick() {
		ticksSinceCrystalSwap.incrementAndGet();
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (mc.player == null) {
			return;
		}

		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
			int slot = packet.getSelectedSlot();

			if (InvUtils.getStack(slot).getItem() == Items.END_CRYSTAL) {
				ticksSinceCrystalSwap.set(0);
			}
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
			setBackTimer.reset();
		}
	}

	public boolean isCcSwapDelayPassed() {
		return getTicksSinceCrystalSwap() >= CC_CRYSTAL_SWAP_DELAY;
	}

	private int getTicksSinceCrystalSwap() {
		return ticksSinceCrystalSwap.get();
	}

	public boolean passed(int millis) {
		return setBackTimer.passedMillis(millis);
	}
}
