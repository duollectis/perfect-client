package com.perfect.client.core.utils.game;

import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.mc;

public class PacketUtils {

	public static final int TOTEM_POP_STATUS_ID = 35;

	public static void sendPacket(Packet<?> packet) {
		if (packet == null || mc.player == null || mc.player.networkHandler == null) {
			return;
		}

		mc.player.networkHandler.sendPacket(packet);
	}

	public static void sendSequencedPacket(SequencedPacketCreator packetCreator) {
		if (packetCreator == null || mc.player == null || mc.player.networkHandler == null || mc.world == null) {
			return;
		}

		try (PendingUpdateManager pendingUpdateManager = mc.world.pendingUpdateManager) {
			pendingUpdateManager.incrementSequence();
			int i = pendingUpdateManager.getSequence();
			mc.player.networkHandler.sendPacket(packetCreator.predict(i));
		}
	}

	public static Vec3d getPos(EntitySpawnS2CPacket packet) {
		return new Vec3d(packet.getX(), packet.getY(), packet.getZ());
	}

	public static boolean isTotemPop(EntityStatusS2CPacket packet) {
		return packet.getStatus() == TOTEM_POP_STATUS_ID;
	}

	public static boolean isNormalSlot(int slot) {
		if (mc.player == null) {
			return false;
		}

		if (slot < 0) {
			return false;
		}

		return mc.player.getInventory().size() > slot;
	}

	public static void sendSneaking(boolean sneak) {
		if (mc.player == null) {
			return;
		}

		PlayerInput input = mc.player.input.playerInput;

		sendPacket(new PlayerInputC2SPacket(
				new PlayerInput(
						input.forward(),
						input.backward(),
						input.left(),
						input.right(),
						input.jump(),
						sneak,
						input.sprint()
				)
		));
	}
}
