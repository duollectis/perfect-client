package com.perfect.client.core.render.animation.world.scan.direction;

import net.minecraft.util.math.Direction;

import java.util.HashMap;

public class DrawDirections {

	private static final HashMap<Direction, DrawDirection> DRAW_DIRECTIONS = new HashMap<>();

	private static boolean initialized;

	public static DrawDirection get(Direction direction) {
		if (!initialized) {
			DRAW_DIRECTIONS.put(Direction.EAST, new XBasedDraw(true));
			DRAW_DIRECTIONS.put(Direction.WEST, new XBasedDraw(false));
			DRAW_DIRECTIONS.put(Direction.UP, new YBasedDraw(true));
			DRAW_DIRECTIONS.put(Direction.DOWN, new YBasedDraw(false));
			DRAW_DIRECTIONS.put(Direction.SOUTH, new ZBasedDraw(true));
			DRAW_DIRECTIONS.put(Direction.NORTH, new ZBasedDraw(false));

			initialized = true;
		}

		return DRAW_DIRECTIONS.get(direction);
	}
}
