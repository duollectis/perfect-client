package com.perfect.client.core.utils.functions.hole;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;


public class Hole {

	public final BlockPos pos;
	public final HoleType type;
	public final BlockPos[] positions;
	public final Vec3d middle;
	public final Box box;
	public final boolean bedrock;

	public Hole(BlockPos pos, HoleType type) {
		this.pos = pos;
		this.type = type;

		switch (type) {
			case SINGLE -> {
				positions = new BlockPos[]{pos};
				middle = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				box = new Box(pos);
			}
			case DOUBLE_X -> {
				positions = new BlockPos[]{pos, pos.add(1, 0, 0)};
				middle = new Vec3d(pos.getX() + 1, pos.getY(), pos.getZ() + 0.5);
				box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 1, pos.getZ() + 1);
			}
			case DOUBLE_Z -> {
				positions = new BlockPos[]{pos, pos.add(0, 0, 1)};
				middle = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 1);
				box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 2);
			}
			case QUAD -> {
				positions = new BlockPos[]{pos, pos.add(1, 0, 0), pos.add(0, 0, 1), pos.add(1, 0, 1)};
				middle = new Vec3d(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
				box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 2, pos.getY() + 1, pos.getZ() + 2);
			}
			default -> {
				positions = new BlockPos[]{};
				middle = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				box = new Box(pos);
			}
		}

		bedrock = HoleUtils.isBedrock(positions);
	}

	public BlockPos[] positions() {
		return positions;
	}

	public boolean containsPosition(BlockPos pos) {
		for (BlockPos holePos : positions) {
			if(pos.equals(holePos)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Hole hole) {
			return Arrays.equals(positions, hole.positions);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}