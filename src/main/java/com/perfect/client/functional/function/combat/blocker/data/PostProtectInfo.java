package com.perfect.client.functional.function.combat.blocker.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor
public class PostProtectInfo {

	private final BlockPos blockPos;
	private final Vec3d damageVec3d;
}
