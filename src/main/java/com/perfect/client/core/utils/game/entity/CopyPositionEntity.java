package com.perfect.client.core.utils.game.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;
import java.util.UUID;

import static com.perfect.client.PerfectClient.mc;

public class CopyPositionEntity extends OtherClientPlayerEntity {

	public CopyPositionEntity(PlayerEntity player, String name) {
		super(Objects.requireNonNull(mc.world), new GameProfile(UUID.randomUUID(), name));

		copyPositionAndRotation(player);

		lastYaw = getYaw();
		lastPitch = getPitch();
		headYaw = player.headYaw;
		lastHeadYaw = headYaw;
		bodyYaw = player.bodyYaw;
		lastBodyYaw = bodyYaw;

		Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
		dataTracker.set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);

		getAttributes().setFrom(player.getAttributes());
		setPose(player.getPose());

		capeX = getX();
		capeY = getY();
		capeZ = getZ();
	}
}
