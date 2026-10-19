package com.perfect.client.functional.function.visuals.holeEsp.data;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;


public class HoleChunk {

	// По координатам X и Z повторяет обычные чанки, но также подразделяется на чанки по Y.
	private final int x;
	private final int y;
	private final int z;

	public HoleChunk(Vec3i pos) {
		this(pos.getX(), pos.getY(), pos.getZ());
	}

	public HoleChunk(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public List<BlockPos> getBlocks() {
		int minBlockX = x * 16;
		int minBlockY = y * 16;
		int minBlockZ = z * 16;

		List<BlockPos> blocks = new ArrayList<>();

		for (int x = minBlockX; x < minBlockX + 16; x++) {
			for (int y = minBlockY; y < minBlockY + 16; y++) {
				for (int z = minBlockZ; z < minBlockZ + 16; z++) {
					blocks.add(new BlockPos(x, y, z));
				}
			}
		}

		return blocks;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof HoleChunk chunk)) {
			return false;
		}
		return chunk.x == x && chunk.y == y && chunk.z == z;
	}

	@Override
	public int hashCode() {
		return (y + z * 31) * 31 + x;
	}
}
