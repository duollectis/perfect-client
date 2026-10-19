package com.perfect.client.functional.function.combat.autoWeb.data;

import lombok.Getter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

@Getter
public class PlaceInfo {

	private final BlockPos pos;
	private final BlockHitResult hitResult;

	public PlaceInfo(BlockPos pos, BlockHitResult hitResult) {
		this.pos = pos;
		this.hitResult = hitResult;
	}
}
