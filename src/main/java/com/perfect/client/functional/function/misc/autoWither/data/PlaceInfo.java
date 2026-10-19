package com.perfect.client.functional.function.misc.autoWither.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

@Getter
@Setter
public class PlaceInfo {

	private final BlockPos pos;
	private final Block block;

	private HitResult hitResult;

	public PlaceInfo(BlockPos pos, Block block) {
		this.pos = pos;
		this.block = block;
	}
}
