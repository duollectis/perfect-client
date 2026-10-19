package com.perfect.client.core.utils.game.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;


public class ChunkUtils {

	public static Vec3i blockPosToChunkPos(BlockPos pos) {
		int x = pos.getX() >> 4;
		int y = pos.getY() >> 4;
		int z = pos.getZ() >> 4;
		return new Vec3i(x, y, z);
	}
}
