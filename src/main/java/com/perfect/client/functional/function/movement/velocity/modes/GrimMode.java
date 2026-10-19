package com.perfect.client.functional.function.movement.velocity.modes;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.movement.velocity.Velocity;
import com.perfect.client.functional.function.movement.velocity.VelocityConfig;
import com.perfect.client.functional.function.movement.velocity.utils.VelocityMode;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Direction;


public class GrimMode extends VelocityMode {

	private boolean cancelVelocity;

	public GrimMode(
			Velocity function,
			VelocityConfig config
	) {
		super(function, config);
	}

	@Override
	public void onEnable() {
		cancelVelocity = false;
	}

	@Override
	public void onDisable() {
		cancelVelocityIfNeed();
	}

	@Override
	public void onEntityVelocityUpdate(Cancellable event, EntityVelocityUpdateS2CPacket packet) {
		cancelEventGrim(event, packet);
	}

	@Override
	public void onExplosion(Cancellable event, ExplosionS2CPacket packet) {
		cancelEventGrim(event, packet);
	}

	@Override
	public void onClientPlayerUpdate() {
		cancelVelocityIfNeed();
	}

	private void cancelEventGrim(Cancellable event, Packet<?> packet) {
		if (canCancelEvent()) {
			function.cancelVelocity(event, packet);
			cancelVelocity = true;
		}
	}

	private boolean canCancelEvent() {
		return Managers.ANTI_CHEAT.passed(100);
	}

	private void cancelVelocityIfNeed() {
		if (!cancelVelocity) {
			return;
		}
		doVelocityCancel();
		cancelVelocity = false;
	}

	
	private void doVelocityCancel() {
		if (mc.player == null) {
			return;
		}

		if (function.isPhased()) {
			return;
		}

		float yaw = mc.player.getYaw();
		float pitch = mc.player.getPitch();

		sendPacket(new PlayerMoveC2SPacket.Full(
				mc.player.getX(),
				mc.player.getY(),
				mc.player.getZ(),
				yaw,
				pitch,
				mc.player.isOnGround(),
				mc.player.horizontalCollision
		));

		sendPacket(new PlayerActionC2SPacket(
				PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
				mc.player.isCrawling() ? mc.player.getBlockPos() : mc.player.getBlockPos().up(),
				Direction.DOWN
		));
	}
}
