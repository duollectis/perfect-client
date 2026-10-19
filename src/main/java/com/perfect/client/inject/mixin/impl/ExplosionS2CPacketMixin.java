package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IExplosionS2CPacket;
import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ExplosionS2CPacket.class)
public abstract class ExplosionS2CPacketMixin implements IExplosionS2CPacket {

	@Shadow
	public abstract Optional<Vec3d> playerKnockback();

	@Override
	public void perfect$setVelocity(Vec3d velocity) {
		if (playerKnockback().isPresent()) {
			((IVec3d) playerKnockback().get()).perfect$set(velocity.x, velocity.y, velocity.z);
		}
	}
}
