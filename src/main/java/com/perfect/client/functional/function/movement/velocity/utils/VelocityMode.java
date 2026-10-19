package com.perfect.client.functional.function.movement.velocity.utils;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.movement.velocity.Velocity;
import com.perfect.client.functional.function.movement.velocity.VelocityConfig;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;

public class VelocityMode extends FunctionHandler<Velocity, VelocityConfig> {

	public VelocityMode(Velocity function, VelocityConfig config) {
		super(function, config);
	}

	public void onEntityVelocityUpdate(Cancellable event, EntityVelocityUpdateS2CPacket packet) {}

	public void onExplosion(Cancellable event, ExplosionS2CPacket packet) {}

	public void onEntityDamage(Cancellable event, EntityDamageS2CPacket packet) {}
}
