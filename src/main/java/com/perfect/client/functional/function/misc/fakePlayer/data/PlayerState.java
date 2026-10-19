package com.perfect.client.functional.function.misc.fakePlayer.data;

import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.Vec3d;

public record PlayerState(EntityPose pose, Vec3d pos, float yaw, float pitch) {
}