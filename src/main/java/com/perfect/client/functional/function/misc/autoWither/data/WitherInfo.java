package com.perfect.client.functional.function.misc.autoWither.data;

import lombok.Getter;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

@Getter
public class WitherInfo {

	private final List<PlaceInfo> blocks = new ArrayList<>();

	private final BlockPos center;
	private final boolean rotate;

	public WitherInfo(BlockPos center, boolean rotate) {
		this.center = center;
		this.rotate = rotate;
	}

	public void add(PlaceInfo placeInfo) {
		blocks.add(placeInfo);
	}
}
