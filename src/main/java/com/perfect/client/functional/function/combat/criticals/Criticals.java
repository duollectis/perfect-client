package com.perfect.client.functional.function.combat.criticals;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.inject.mixinInterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.block.CobwebBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import static com.perfect.client.manager.system.FunctionManager.attackAura;


public class Criticals extends Function {

	private final CriticalsConfig config = new CriticalsConfig(this);

	private final TimerUtils attackTimer = new TimerUtils();

	public Criticals() {
		super("criticals", "Criticals", Category.COMBAT);
	}


	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (mc.world == null || mc.player == null) {
			return;
		}

		if (config.getOnlyWithAttackAura().get() && !attackAura.isEnabled()) {
			return;
		}

		if (!isGoodPacket(event.getPacket())) {
			return;
		}

		if (shouldSkipCrit()) {
			return;
		}

		if (shouldPause()) {
			return;
		}

		setCriticalPackets();
	}

	private void setCriticalPackets() {
		if (mc.world == null || mc.player == null) {
			return;
		}

		switch (config.getMode().get()) {
			case PACKET -> sendPacketMode();
			case NCP -> sendNcpMode();
			case GRIM -> sendGrimMode();
			case GRIM_V3 -> sendGrimV3Mode();
		}
	}

	
	private void sendPacketMode() {
		if (mc.player == null) {
			return;
		}

		if (mc.player.isOnGround()) {
			sendPacket(0.0625);
			sendPacket(0.0);
		}
	}

	
	private void sendGrimMode() {
		if (mc.player == null) {
			return;
		}

		if (attackTimer.passedMillis(250)) {
			float yaw = Managers.ROTATION.getSentYaw();
			float pitch = Managers.ROTATION.getSentPitch();

			if (Managers.ROTATION.isRotating()) {
				yaw = Managers.ROTATION.getRotateYaw();
				pitch = Managers.ROTATION.getRotatePitch();
			}

			sendPacketFull(0.0625, yaw, pitch, false);
			sendPacketFull(0.0625013579, yaw, pitch, false);
			sendPacketFull(1.3579e-6, yaw, pitch, false);

			attackTimer.reset();
		}
	}

	
	private void sendGrimV3Mode() {
		if (mc.player == null) {
			return;
		}

		float yaw = Managers.ROTATION.getSentYaw();
		float pitch = Managers.ROTATION.getSentPitch();

		if (Managers.ROTATION.isRotating()) {
			yaw = Managers.ROTATION.getRotateYaw();
			pitch = Managers.ROTATION.getRotatePitch();
		}

		sendPacketFull(0, yaw, pitch, true);
		sendPacketFull(0.0625f, yaw, pitch, false);
		sendPacketFull(0.04535f, yaw, pitch, false);

		attackTimer.reset();
	}

	
	private void sendNcpMode() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (PlayerUtils.isInWeb()) {
			if (mc.world.getBlockState(mc.player.getBlockPos()).getBlock() instanceof CobwebBlock) {
				sendPacket(0.0625101);
				sendPacket(0.0);
				sendPacket(0.0125);
				sendPacket(0.0);
			}
		}

		//		sendPacket(0.11);
		//		sendPacket(0.1100013579);
		//		sendPacket(0.0000013579);

		sendPacket(0.062602401692772);
		sendPacket(0.0726023996066094);
		sendPacket(0.0);
	}

	private boolean isGoodPacket(Packet<?> p) {
		return p instanceof IPlayerInteractEntityC2SPacket packet &&
				packet.perfect$isAttack() &&
				packet.perfect$getEntity() instanceof LivingEntity;
	}

	private void sendPacket(double height) {
		if (mc.player == null) {
			return;
		}

		PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(
				mc.player.getX(),
				mc.player.getY() + height,
				mc.player.getZ(),
				false,
				mc.player.horizontalCollision
		);

		sendPacket(packet);
	}

	private void sendPacketFull(double height, float yaw, float pitch, boolean onGround) {
		if (mc.player == null) {
			return;
		}

		PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(
				mc.player.getX(),
				mc.player.getY() + height,
				mc.player.getZ(),
				yaw, pitch,
				onGround,
				mc.player.horizontalCollision
		);

		sendPacket(packet);
	}

	private boolean shouldSkipCrit() {
		return mc.player == null ||
				!mc.player.isOnGround() ||
				mc.player.isSubmergedInWater() ||
				mc.player.isInLava() ||
				mc.player.isClimbing();
	}

	private boolean shouldPause() {
		assert mc.player != null;

		if (config.getPauseInWeb().get() && PlayerUtils.isInWeb()) {
			return true;
		}

		if (config.getMode().get(Mode.GRIM) || config.getMode().get(Mode.GRIM_V3)) {
			if (!mc.player.isOnGround() || mc.player.isCrawling()) {
				return true;
			}

			if (config.getOnlyPhased().get() && (config.getOnlyPhasedWalls().get() ? !PlayerUtils.isDoublePhased() : !PlayerUtils.isPhased())) {
				return true;
			}
		}

		return config.getPauseOnMove().get() && (PlayerUtils.isMoving() || PlayerUtils.isInputMoving());
	}

	public enum Mode {
		PACKET,
		NCP,
		GRIM,
		GRIM_V3
	}
}