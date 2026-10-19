package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static com.perfect.client.PerfectClient.mc;

@Mixin(PlayerInteractEntityC2SPacket.class)
public class PlayerInteractEntityC2SPacketMixin implements IPlayerInteractEntityC2SPacket {

	@Shadow
	@Final
	private int entityId;

	@Shadow
	@Final
	private PlayerInteractEntityC2SPacket.InteractTypeHandler type;

	@Override
	public boolean perfect$isAttack() {
		return type.getType() == PlayerInteractEntityC2SPacket.InteractType.ATTACK;
	}

	@Override
	public boolean perfect$isInteract() {
		return type.getType() == PlayerInteractEntityC2SPacket.InteractType.INTERACT;
	}

	@Override
	public boolean perfect$isInteractAt() {
		return type.getType() == PlayerInteractEntityC2SPacket.InteractType.INTERACT_AT;
	}

	@Override
	public Entity perfect$getEntity() {
		if (mc.world == null) {
			return null;
		}

		return mc.world.getEntityById(entityId);
	}
}
