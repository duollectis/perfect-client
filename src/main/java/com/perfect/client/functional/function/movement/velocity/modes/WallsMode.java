package com.perfect.client.functional.function.movement.velocity.modes;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.functional.function.movement.velocity.Velocity;
import com.perfect.client.functional.function.movement.velocity.VelocityConfig;
import com.perfect.client.functional.function.movement.velocity.utils.VelocityMode;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;


public class WallsMode extends VelocityMode {

	public WallsMode(
			Velocity function,
			VelocityConfig config
	) {
		super(function, config);
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
