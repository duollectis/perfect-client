package com.perfect.client.functional.function.player.hitboxDesync.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.functional.function.player.hitboxDesync.HitboxDesync;
import com.perfect.client.functional.function.player.hitboxDesync.HitboxDesyncConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectionHelper extends FunctionHelper<HitboxDesync, HitboxDesyncConfig> {

	public DirectionHelper(HitboxDesync function, HitboxDesyncConfig config) {
		super(function, config);
	}

	public List<Direction> getDirections() {
		return switch (config.getLogic().get()) {
			case LOOK -> getDirectionsByLook();
			case POSITION -> getDirectionsByPosition();
		};
	}

	
	private List<Direction> getDirectionsByPosition() {
		if (mc.player == null) {
			return Collections.emptyList();
		}

		BlockPos blockPos = mc.player.getBlockPos();
		Vec3d playerPos = mc.player.getPos();

		Vec3d p = playerPos.add(-blockPos.getX(), 0, -blockPos.getZ());

		List<Direction> directions = new ArrayList<>();

		if(p.x > 0.5) {
			directions.add(Direction.EAST);
		}
		else {
			directions.add(Direction.WEST);
		}

		if(p.z > 0.5) {
			directions.add(Direction.SOUTH);
		}
		else {
			directions.add(Direction.NORTH);
		}
		return directions;
	}

	
	private List<Direction> getDirectionsByLook() {
		if (mc.player == null) {
			return Collections.emptyList();
		}

		float yaw = mc.player.getYaw() % 360;
		if (yaw < 0) {
			yaw += 360;
		}

		List<Direction> directions = new ArrayList<>();

		if (yaw > 0 && yaw < 22.5) {
			directions.add(Direction.SOUTH);
		}
		else if (yaw > 22.5 && yaw < 67.5) {
			directions.add(Direction.SOUTH);
			directions.add(Direction.WEST);
		}
		else if (yaw > 67.5 && yaw < 112.5) {
			directions.add(Direction.WEST);
		}
		else if (yaw > 112.5 && yaw < 157.5) {
			directions.add(Direction.WEST);
			directions.add(Direction.NORTH);
		}
		else if (yaw > 157.5 && yaw < 202.5) {
			directions.add(Direction.NORTH);
		}
		else if (yaw > 202.5 && yaw < 247.5) {
			directions.add(Direction.NORTH);
			directions.add(Direction.EAST);
		}
		else if (yaw > 247.5 && yaw < 292.5) {
			directions.add(Direction.EAST);
		}
		else if (yaw > 292.5 && yaw < 337.5) {
			directions.add(Direction.EAST);
			directions.add(Direction.SOUTH);
		}
		else if (yaw > 337.5 && yaw < 360) {
			directions.add(Direction.SOUTH);
		}

		return directions;
	}
}
