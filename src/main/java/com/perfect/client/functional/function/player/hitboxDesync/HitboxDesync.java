package com.perfect.client.functional.function.player.hitboxDesync;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.functional.function.player.hitboxDesync.helpers.DirectionHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;


public class HitboxDesync extends Function {

	private final HitboxDesyncConfig config = new HitboxDesyncConfig(this);
	private final DirectionHelper directionHelper = new DirectionHelper(this, config);

	public HitboxDesync() {
		super("hitbox-desync", "Hitbox Desync", Category.PLAYER, "Modul ebliviy");
	}

	
	@Override
	public void onPreTick() {
		if (mc.player == null)
			return;

		Vec3d playerPos = mc.player.getPos();

		double x = playerPos.x;
		double z = playerPos.z;

		Vec3d offset = blockSideOffset();

		if (offset.x != 0) {
			x = (int) x;
		}
		if (offset.z != 0) {
			z = (int) z;
		}

		mc.player.setPosition(new Vec3d(x + offset.x, playerPos.y, z + offset.z));

		sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
				mc.player.getX(),
				mc.player.getY(),
				mc.player.getZ(),
				mc.player.isOnGround(),
				mc.player.horizontalCollision
		));

		disable();
	}

	
	private Vec3d blockSideOffset() {
		if (mc.player == null) {
			return Vec3d.ZERO;
		}

		List<Direction> directions = directionHelper.getDirections();
		Vec3d pos = mc.player.getPos();

		double x = 0, z = 0;
		double offset = StringUtils.parseDouble(config.getOffset().get()).orElse(0.0);

		double minCorner = 0.3 - offset;
		double maxCorner = 0.7 + offset;

		if (pos.x >= 0) {
			if (isEast(directions))
				x = maxCorner;
			else if (isWest(directions))
				x = minCorner;
		}
		else {
			if (isEast(directions))
				x = -minCorner;
			else if (isWest(directions))
				x = -maxCorner;
		}

		if (pos.z >= 0) {
			if (isSouth(directions))
				z = maxCorner;
			else if (isNorth(directions))
				z = minCorner;
		}
		else {
			if (isSouth(directions))
				z = -minCorner;
			else if (isNorth(directions))
				z = -maxCorner;
		}
		return new Vec3d(x, 0, z);
	}

	private boolean isSouth(List<Direction> directions) {
		return directions.contains(Direction.SOUTH);
	}

	private boolean isNorth(List<Direction> directions) {
		return directions.contains(Direction.NORTH);
	}

	private boolean isWest(List<Direction> directions) {
		return directions.contains(Direction.WEST);
	}

	private boolean isEast(List<Direction> directions) {
		return directions.contains(Direction.EAST);
	}

	public enum Logic {
		LOOK,
		POSITION
	}
}