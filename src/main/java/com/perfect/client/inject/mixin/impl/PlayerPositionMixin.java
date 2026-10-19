package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IPlayerPosition;
import net.minecraft.entity.player.PlayerPosition;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerPosition.class)
public class PlayerPositionMixin implements IPlayerPosition {

	@Final
	@Shadow
	@Mutable
	private float yaw, pitch;

	@Override
	public void perfect$setLook(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
