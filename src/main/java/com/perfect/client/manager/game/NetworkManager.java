package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import lombok.Getter;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.PlayerInput;

@Getter

public class NetworkManager extends Manager {

	private volatile PlayerInput playerInput;

	private volatile boolean sprinting;
	private volatile boolean shifting;
	private volatile boolean onGround;

	private volatile int slot;

	private volatile boolean inScreenHandler;

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (event.getPacket() instanceof ClientCommandC2SPacket packet) {
			switch (packet.getMode()) {
				case START_SPRINTING -> sprinting = true;
				case STOP_SPRINTING -> sprinting = false;
			}
		}

		if (event.getPacket() instanceof PlayerInputC2SPacket packet) {
			playerInput = packet.input();
			shifting = packet.input().sneak();
		}

		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			onGround = packet.isOnGround();
		}

		if (event.getPacket() instanceof UpdateSelectedSlotC2SPacket packet) {
			slot = packet.getSelectedSlot();
		}


		if (event.getPacket() instanceof ClickSlotC2SPacket packet) {
			if (packet.actionType() == SlotActionType.SWAP) {
				inScreenHandler = true;
			}
		}

		if (event.getPacket() instanceof CloseHandledScreenC2SPacket) {
			inScreenHandler = false;
		}

		// Взаимодействие с FakePlayer.

		if (event.getPacket() instanceof PlayerInteractEntityC2SPacket packet) {
			if (packet.entityId < 0) {
				event.cancel();
			}
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof UpdateSelectedSlotS2CPacket packet) {
			slot = packet.slot();
		}
	}

	@Override
	public void onRespawn() {
		playerInput = null;
		shifting = false;
		sprinting = false;
		slot = 0;
		inScreenHandler = false;
	}

	public boolean isHolding(int slot) {
		return this.slot == slot;
	}
}
