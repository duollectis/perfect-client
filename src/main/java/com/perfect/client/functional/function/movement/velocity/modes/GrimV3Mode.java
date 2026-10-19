package com.perfect.client.functional.function.movement.velocity.modes;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.functional.function.movement.velocity.Velocity;
import com.perfect.client.functional.function.movement.velocity.VelocityConfig;
import com.perfect.client.functional.function.movement.velocity.utils.VelocityMode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;


public class GrimV3Mode extends VelocityMode {

	public GrimV3Mode(Velocity function, VelocityConfig config) {
		super(function, config);
	}

	@Override
	public void onEntityDamage(Cancellable event, EntityDamageS2CPacket packet) {
		if (mc.player == null) {
			return;
		}

		if (packet.entityId() == mc.player.getId() && function.isPhased()) {
			sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, mc.player.horizontalCollision));
			sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
		}
	}

	@Override
	public void onEntityVelocityUpdate(Cancellable event, EntityVelocityUpdateS2CPacket packet) {
		function.cancelVelocityWalls(event, packet);
	}

	@Override
	public void onExplosion(Cancellable event, ExplosionS2CPacket packet) {
		function.cancelVelocityWalls(event, packet);
	}
}
