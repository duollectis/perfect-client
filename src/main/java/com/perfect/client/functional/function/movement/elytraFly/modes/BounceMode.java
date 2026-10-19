package com.perfect.client.functional.function.movement.elytraFly.modes;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.movement.JumpVelocityMultiplierEvent;
import com.perfect.client.core.event.events.movement.TravelEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.movement.elytraFly.ElytraFly;
import com.perfect.client.functional.function.movement.elytraFly.ElytraFlyConfig;
import com.perfect.client.functional.function.movement.elytraFly.utils.FlyMode;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


public class BounceMode extends FlyMode {

	public BounceMode(
			ElytraFly function,
			ElytraFlyConfig config
	) {
		super(function, config);
	}

	private float prevClientYaw;
	private float prevClientPitch;

	private boolean rubberbanded = false;
	private final TimerUtils rubberbandTimer = new TimerUtils();

	@Override
	public void onDisable() {
		rubberbanded = false;
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (config.getAutoJump().get()) {
			event.jumping.set(true);
		}

		event.forward.set(true);

		if (!config.getSprint().get()) {
			if (mc.player.isGliding()) {
				event.sprinting.set(mc.player.isOnGround());
			}
			else {
				event.sprinting.set(true);
			}
		}
	}

	
	@Override
	public void onPostTick() {
		if (mc.player == null) {
			return;
		}

		if (mc.player.input.playerInput.jump() && !mc.player.isGliding() && config.getAutoTakeoff().get()) {
			PacketUtils.sendPacket(
					new ClientCommandC2SPacket(
							mc.player,
							ClientCommandC2SPacket.Mode.START_FALL_FLYING
					)
			);
		}

		if (PlayerUtils.canElytraTakeoff(true)) {
			if (!rubberbanded) {
				if (config.getAccelerate().get() && mc.player.isGliding()) {
					accelerateSpeed();
				}
			}

			if (rubberbanded && config.getRestart().get()) {
				if (rubberbandTimer.passedTicks(config.getRestartDelay().get())) {
					PacketUtils.sendPacket(
							new ClientCommandC2SPacket(
									mc.player,
									ClientCommandC2SPacket.Mode.START_FALL_FLYING
							)
					);

					rubberbanded = false;
				}
			}
		}
	}

	@Override
	public void onPreTick() {
		if (mc.player == null) {
			return;
		}

		if (PlayerUtils.canElytraTakeoff(true) && config.getSprint().get()) {
			mc.player.setSprinting(true);
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.player != null && event.getPacket() instanceof PlayerPositionLookS2CPacket) {
			mc.player.stopGliding();

			rubberbanded = true;
			rubberbandTimer.reset();
		}
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (mc.player != null && event.getPacket() instanceof ClientCommandC2SPacket clientCommandC2SPacket
				&& clientCommandC2SPacket
				.getMode()
				.equals(ClientCommandC2SPacket.Mode.START_FALL_FLYING) && !config.getSprint().get()) {
			mc.player.setSprinting(true);
		}
	}

	@Override
	public void onPreSendMovement() {
		if (mc.player == null) {
			return;
		}

		if (config.getChangeYaw().get()) {
			mc.player.setYaw(config.getYaw().get().floatValue());
		}

		if (config.getChangePitch().get()) {
			mc.player.setPitch(config.getPitch().get().floatValue());
		}
	}

	@Override
	public void onPreTravel(TravelEvent event) {
		if (mc.player == null) {
			return;
		}

		prevClientYaw = mc.player.getYaw();
		prevClientPitch = mc.player.getPitch();

		if (config.getChangeYaw().get()) {
			mc.player.setYaw(config.getYaw().get().floatValue());
		}

		if (config.getChangePitch().get()) {
			mc.player.setPitch(config.getPitch().getFloat());
		}
	}

	@Override
	public void onPostTravel(TravelEvent event) {
		if (mc.player == null) {
			return;
		}

		if (config.getChangeYaw().get()) {
			mc.player.setYaw(prevClientYaw);
		}

		if (config.getChangePitch().get()) {
			mc.player.setPitch(prevClientPitch);
		}
	}

	@Override
	public void onJumpVelocityMultiplier(JumpVelocityMultiplierEvent event) {
		if (config.getCustomJump().get()) {
			event.setMultiplier(config.getJumpMultiplier().get().floatValue());
		}
	}

	public static boolean recastElytra() {
		assert mc.player != null;

		if (PlayerUtils.canElytraTakeoff(true) && startGliding()) {
			PacketUtils.sendPacket(
					new ClientCommandC2SPacket(
							mc.player,
							ClientCommandC2SPacket.Mode.START_FALL_FLYING
					)
			);

			return true;
		}
		else {
			return false;
		}
	}

	private static boolean startGliding() {
		assert mc.player != null;

		for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
			if (LivingEntity.canGlideWith(mc.player.getEquippedStack(equipmentSlot), equipmentSlot)) {
				mc.executeSync(mc.player::startGliding);

				return true;
			}
		}

		return false;
	}

	
	private void accelerateSpeed() {
		assert mc.player != null;

		Vec3d velocity = mc.player.getVelocity();
		double playerSpeed = Math.pow(velocity.getX(), 2.0) + Math.pow(velocity.getZ(), 2.0);

		if (playerSpeed <= Math.pow(config.getMaxSpeed().get(), 2.0)) {
			float rads = (float) Math.toRadians(mc.player.getYaw());
			double xVelocity = MathHelper.sin(rads) * config.getBoost().get() * -0.02;
			double zVelocity = MathHelper.cos(rads) * config.getBoost().get() * 0.02;

			mc.player.addVelocity(
					xVelocity,
					0.0,
					zVelocity
			);
		}
	}
}
