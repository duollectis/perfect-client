package com.perfect.client.functional.function.player.antiAcDetect.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetect;
import com.perfect.client.functional.function.player.antiAcDetect.AntiAcDetectConfig;
import com.perfect.client.functional.function.player.antiAcDetect.utils.AntiAcDetectHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

// Ванильный клиент не отправляет несколько пакетов с ротацией подряд с одинаковым yaw и pitch.
// Обратный случай грим детектит как AimDuplicateLook.


public class AimDuplicateHandler extends AntiAcDetectHandler {

	// Ванильный клиент всегда отправляет пакет с Position раз в количество тиков даже когда игрок стоит.
	// Если отменять этот пакет, то грим детектит BadPacketsE.
	private static final int MAX_NO_POSITION_TICKS = 19;

	private int noPositionPackets;

	private boolean lastSetback;

	public AimDuplicateHandler(AntiAcDetect function, AntiAcDetectConfig config) {
		super(function, config);
	}

	// Если изменять пакет после телепорта, то грим детектит BadPacketsN.
	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
			lastSetback = true;
		}
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			onPlayerMovePacket(event, packet);
		}
	}

	private void onPlayerMovePacket(Cancellable event, PlayerMoveC2SPacket packet) {
		if (lastSetback) {
			lastSetback = false;
			return;
		}

		if (modifyPacket(packet)) {
			event.cancel();
		}
		else if (!packet.changesPosition()) {
			noPositionPackets++;
		}
		else {
			noPositionPackets = 0;
		}
	}

	
	private boolean modifyPacket(PlayerMoveC2SPacket packet) {
		if (mc.player == null) {
			return false;
		}

		if (!packet.changesLook()) {
			return false;
		}

		if (!isDuplicateLook(packet)) {
			return false;
		}

		if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround) {
			sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(packet.isOnGround(), packet.horizontalCollision()));
			return true;
		}
		if (packet instanceof PlayerMoveC2SPacket.Full) {
			if (noPositionPackets > MAX_NO_POSITION_TICKS) {
				return false;
			}

			sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
					packet.getX(mc.player.getX()),
					packet.getY(mc.player.getY()),
					packet.getZ(mc.player.getZ()),
					packet.isOnGround(),
					packet.horizontalCollision()
			));
			return true;
		}
		return false;
	}

	private boolean isDuplicateLook(PlayerMoveC2SPacket packet) {
		assert mc.player != null;

		float yaw = packet.getYaw(mc.player.getYaw());
		float pitch = packet.getPitch(mc.player.getPitch());

		return Managers.ROTATION.getSentYaw() == yaw && Managers.ROTATION.getSentPitch() == pitch;
	}
}
