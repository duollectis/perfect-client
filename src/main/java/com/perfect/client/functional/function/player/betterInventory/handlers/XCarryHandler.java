package com.perfect.client.functional.function.player.betterInventory.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.functional.function.player.betterInventory.BetterInventory;
import com.perfect.client.functional.function.player.betterInventory.BetterInventoryConfig;
import com.perfect.client.functional.function.player.betterInventory.utils.BetterInventoryHandler;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;


public class XCarryHandler extends BetterInventoryHandler {

	private boolean inInventoryScreen;

	public XCarryHandler(BetterInventory function, BetterInventoryConfig config) {
		super(function, config);
	}

	@Override
	public void onEnable() {
		inInventoryScreen = false;
	}

	
	@Override
	public void onDisable() {
		if (inInventoryScreen && mc.player != null) {
			PacketUtils.sendPacket(new CloseHandledScreenC2SPacket(mc.player.playerScreenHandler.syncId));
		}
	}

	@Override
	public void onGameJoin() {
		inInventoryScreen = false;
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (mc.player == null) {
			return;
		}

		if (config.getXCarry().get() && event.getPacket() instanceof CloseHandledScreenC2SPacket packet) {
			if (packet.getSyncId() == mc.player.playerScreenHandler.syncId) {
				inInventoryScreen = true;
				event.cancel();
			}
		}
	}
}
