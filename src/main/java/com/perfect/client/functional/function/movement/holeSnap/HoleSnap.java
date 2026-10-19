package com.perfect.client.functional.function.movement.holeSnap;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.hole.Hole;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;

import java.util.List;

import static com.perfect.client.core.utils.game.rotation.RotationUtils.getYaw;
import static com.perfect.client.manager.system.FunctionManager.tickShift;


public class HoleSnap extends Function {

	private static final int MIN_HOLE_HEIGHT = 3;

	private boolean enabledTickShift;

	private final HoleSnapConfig config = new HoleSnapConfig(this);

	public HoleSnap() {
		super("hole-snap", "Hole Snap", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabledTickShift = !tickShift.isEnabled();

		if (config.getTickShift().get()) {
			tickShift.enable();
		}
	}

	@Override
	public void onDisable() {
		if (config.getTickShift().get() && enabledTickShift) {
			tickShift.disable();
		}

		enabledTickShift = false;
	}

	@Override
	public void onRotate() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		Hole hole = findHole();

		if (hole != null) {
			Managers.ROTATION.setRotateYaw(getYaw(hole.middle));
		}
	}

	
	@Override
	public void onHoleSnapPostPlayerMove(PlayerMoveEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		Vec3d movement = event.getMovement();
		double speed = movement.horizontalLength();

		Hole hole = findHole();

		if (hole == null) {
			error("No hole found");
			disable();
			return;
		}

		if (mc.player.getX() == hole.middle.x && mc.player.getZ() == hole.middle.z) {
			if (mc.player.getY() == hole.middle.y) {
				disable();
				event.setX(0.0);
				event.setZ(0.0);
			}
			else if (isOnGround()) {
				disable();
			}
			else {
				event.setX(0.0);
				event.setZ(0.0);
			}
		}
		else {
			double rad = Math.toRadians(getYaw(hole.middle) + 90.0);

			double x = speed * Math.cos(rad);
			double dX = hole.middle.x - mc.player.getX();
			double z = speed * Math.sin(rad);
			double dZ = hole.middle.z - mc.player.getZ();

			event.setX(Math.abs(x) < Math.abs(dX) ? x : dX);
			event.setZ(Math.abs(z) < Math.abs(dZ) ? z : dZ);
		}
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (findHole() != null) {
			event.forward.set(true);
			event.back.set(false);
			event.right.set(false);
			event.left.set(false);
		}
	}

	private Hole findHole() {
		if (mc.player == null) {
			return null;
		}

		Hole closest = null;
		double bestDistance = 0.0;

		List<BlockPos> cube = PositionMath.getCube(
				config.getRangeXZ().get(),
				config.getRangeY().get()
		);

		for (BlockPos pos : cube) {
			Hole hole = HoleUtils.getHole(
					pos,
					config.getHoleTypes().get(HoleTypes.SINGLE),
					config.getHoleTypes().get(HoleTypes.DOUBLE),
					config.getHoleTypes().get(HoleTypes.QUAD),
					MIN_HOLE_HEIGHT,
					true
			);

			if (hole == null) {
				continue;
			}

			if (!isBlastResist(hole)) {
				continue;
			}

			if (pos.getY() == 0 && inHole(hole)) {
				return hole;
			}

			if (config.getOnlyVisibleHoles().get() && !isVisibleHole(hole)) {
				continue;
			}

			double distance = PositionMath.squareDistanceHorizontal(hole.middle, mc.player.getPos());

			if (closest == null || distance < bestDistance) {
				closest = hole;
				bestDistance = distance;
			}
		}

		return closest;
	}

	private boolean isVisibleHole(Hole hole) {
		if (mc.player == null) {
			return false;
		}

		Box box = hole.box.expand(-EntityUtils.ANTI_FLOOR_OFFSET);

		return isVisibleArea(new Vec3d(box.minX, box.maxY, box.minZ))
				|| isVisibleArea(new Vec3d(box.maxX, box.maxY, box.minZ))
				|| isVisibleArea(new Vec3d(box.maxX, box.maxY, box.maxZ))
				|| isVisibleArea(new Vec3d(box.minX, box.maxY, box.maxZ));
	}

	private boolean isVisibleArea(Vec3d holeFloorVec3d) {
		assert mc.player != null;

		BlockPos pos = BlockPos.ofFloored(holeFloorVec3d);

		double headY = mc.player.getBoundingBox().getLengthY();

		BlockPos headPos = pos.up((int) Math.ceil(headY));

		Vec3d headVec3d = mc.player.getPos().add(0, headY, 0);
		Vec3d holeCeilVec3d = holeFloorVec3d.add(0, headY, 0);

		return isVisibleVec3d(mc.player.getPos().add(0, 1, 0), holeFloorVec3d, pos)
				&& isVisibleVec3d(headVec3d, holeCeilVec3d, headPos);
	}

	private boolean isVisibleVec3d(Vec3d start, Vec3d end, BlockPos pos) {
		if (mc.player == null) {
			return false;
		}

		RaycastContext context = new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				mc.player
		);

		BlockHitResult raycast = DamageCalculator.raycast(
				context,
				false,
				false,
				pos,
				null
		);

		if (raycast == null) {
			return true;
		}

		return raycast.getType() != HitResult.Type.BLOCK;
	}

	private boolean isBlastResist(Hole hole) {
		if (mc.world == null)
			return false;
		for (BlockPos pos : hole.positions()) {
			for (Direction direction : Direction.values()) {
				if (direction == Direction.UP)
					continue;
				if (HoleUtils.isHoleBlock(pos.offset(direction))
						&& !BlockUtils.isBlastResist(pos.offset(direction)))
					return false;
			}
		}
		return true;
	}

	private boolean inHole(Hole hole) {
		if (mc.player == null)
			return false;
		for (BlockPos pos : hole.positions) {
			if (mc.player.getBlockPos().equals(pos))
				return true;
		}
		return false;
	}

	public boolean isOnGround() {
		if (mc.world == null || mc.player == null) {
			return false;
		}

		Box box = mc.player.getBoundingBox().offset(0, -0.05, 0);
		Iterable<VoxelShape> collisions = mc.world.getBlockCollisions(mc.player, box);

		return collisions.iterator().hasNext();
	}

	public enum Mode {
		MOTION,
		ROTATE
	}

	public enum HoleTypes {
		SINGLE,
		DOUBLE,
		QUAD
	}
}

