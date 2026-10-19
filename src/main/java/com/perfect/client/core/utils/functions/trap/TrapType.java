package com.perfect.client.core.utils.functions.trap;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Getter

public enum TrapType {
	FACE(true, false, 1),
	HEAD(false, true, 1),
	FLOOR(false, true, -1),
	TRAP(true, true, 1),
	TRAP_WITH_ANTI_STEP(true, true, 1),
	SURROUND(true, true, -1),
	SURROUND_WITHOUT_FLOOR(true, false, -1);

	private final int floorHeight;
	private final boolean floor;
	private final boolean surrounding;

	TrapType(boolean surrounding, boolean floor, int floorHeight) {
		this.surrounding = surrounding;
		this.floor = floor;
		this.floorHeight = floorHeight;
	}

	public BlockPos trapCenter(LivingEntity entity) {
		if (this == SURROUND || this == SURROUND_WITHOUT_FLOOR) {
			Vec3d p = entity.getPos();
			return new BlockPos((int) Math.floor(p.getX()), (int) Math.round(p.getY()), (int) Math.floor(p.getZ()));
		}
		return BlockPos.ofFloored(entity.getEyePos());
	}
}