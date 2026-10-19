package com.perfect.client.functional.function.combat.pistonPush;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.collection.UpdatingCollection;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.target.TargetPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;

import static com.perfect.client.core.utils.game.world.BlockUtils.*;
import static com.perfect.client.core.utils.math.PositionMath.directionFromBlock;


public class PistonPush extends Function {

	private final PistonPushConfig config = new PistonPushConfig(this);

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	);

	private Entity target;

	private BlockPos pistonPos, activatorPos, obsidianPos, targetPos;
	private Direction direction;

	private final UpdatingCollection<Entity> pendingPushes = new UpdatingCollection<>();

	public PistonPush() {
		super("piston-push", "Piston Push", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		reset();
		pendingPushes.clear();
	}

	@Override
	public void onPostTick() {
		if (mc.world == null || mc.player == null) {
			return;
		}
		pistonPos = null;

		pendingPushes.update(config.getPushDelay().get());

		target = TargetUtils.getTarget(
				config.getPlaceRange().get() + 2,
				TargetPriority.RANGE,
				false,
				this::isGoodTarget
		);

		if (!(target instanceof PlayerEntity player))
			return;

		if (!HoleUtils.isInHole(player) || PlayerUtils.shouldPause(
				config.getPauseOnEat().get(),
				config.getPauseOnBlink().get()
		)) {
			reset();
			return;
		}

		targetPos = new BlockPos(target.getBlockPos().up());
		pistonPos = getPistonPos(targetPos);
		activatorPos = getRedstonePos(pistonPos);
		obsidianPos = targetPos.down();
	}

	@Override
	public void onRotate() {
		if (pistonPos == null || mc.player == null || !config.getRotate().get()) {
			return;
		}

		Vec3d vec3d = PositionMath.closestVec3d(mc.player, pistonPos);
		Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(vec3d));
		Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(vec3d));
	}

	@Override
	public void onPostSendMovement() {
		if (mc.player == null)
			return;

		if (pistonPos != null && activatorPos != null) {
			FindItemResult redstoneFir = InvUtils.find(Items.REDSTONE_BLOCK);
			FindItemResult
					pistonFir =
					InvUtils.find(stack -> stack.getItem() == Items.PISTON || stack.getItem() == Items.STICKY_PISTON);

			if (!redstoneFir.found() || !pistonFir.found())
				return;
			if (redstoneFir.slot() > 8 || pistonFir.slot() > 8)
				return;

			boolean needPiston = !isOf(pistonPos, Blocks.PISTON, Blocks.STICKY_PISTON);
			boolean needRedstone = !isOf(activatorPos, Blocks.REDSTONE_BLOCK);

			int prevSlot = mc.player.getInventory().getSelectedSlot();

			if (needPiston) {
				sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
						getYaw(direction),
						0,
						mc.player.isOnGround(),
						mc.player.horizontalCollision
				));
				BlockHitResult
						pistonResult =
						new BlockHitResult(
								PositionMath.closestVec3d(mc.player, pistonPos),
								Direction.DOWN,
								pistonPos,
								false
						);

				InteractUtils.placeBlockIternal(pistonResult, pistonFir);

				if (config.getRender().get()) {
					renderer.add(pistonPos);
				}
			}

			if (needRedstone) {
				BlockHitResult
						redstoneResult =
						new BlockHitResult(
								PositionMath.closestVec3d(mc.player, pistonPos),
								directionFromBlock(pistonPos, activatorPos),
								pistonPos,
								false
						);

				InteractUtils.placeBlock(redstoneResult, pistonFir, Rotate.NONE);

				if (config.getRender().get()) {
					renderer.add(activatorPos);
				}
			}

			InvUtils.setSlot(prevSlot, false);

			if (config.getHoleFill().get() && obsidianPos != null) {
				FindItemResult obsidianFir = InvUtils.findInHotbar(Items.OBSIDIAN);
				if (obsidianFir.found()) {
					BlockHitResult
							obsidianResult =
							new BlockHitResult(
									PositionMath.closestVec3d(mc.player, obsidianPos),
									Direction.WEST,
									obsidianPos,
									false
							);
					InteractUtils.placeBlock(obsidianResult, obsidianFir, Rotate.NONE);
					if (config.getRender().get()) {
						renderer.add(obsidianPos);
					}
				}
			}

			pendingPushes.put(target);
			reset();

			if (config.getAutoToggle().get()) {
				disable();
			}
		}
	}

	private boolean isGoodTarget(Entity entity) {
		if (mc.player == null || mc.world == null)
			return false;

		if (!(entity instanceof PlayerEntity player))
			return false;
		if (pendingPushes.containsKey(player))
			return false;
		if (!HoleUtils.isInHole(player))
			return false;
		if (config.getAntiSelfPush().get() && player.getBlockPos().equals(mc.player.getBlockPos()))
			return false;
		if (!mc.world.getBlockState(player.getBlockPos().up()).isReplaceable())
			return false;

		BlockPos possiblePiston = getPistonPos(new BlockPos(player.getBlockPos().up()));
		BlockPos possibleActivator = getRedstonePos(possiblePiston);

		return possiblePiston != null && possibleActivator != null;
	}

	private void reset() {
		targetPos = null;
		pistonPos = null;
		activatorPos = null;
		obsidianPos = null;
		direction = null;
	}

	private int getYaw(Direction direction) {
		if (mc.player == null)
			return 0;

		if (direction == null)
			return (int) mc.player.getYaw();
		return switch (direction) {
			case NORTH -> 180;
			case SOUTH -> 0;
			case WEST -> 90;
			default -> -90;
		};
	}

	private BlockPos getRedstonePos(BlockPos pos) {
		if (mc.player == null || mc.world == null)
			return null;

		ArrayList<BlockPos> posList = new ArrayList<>();
		if (pos == null)
			return null;

		for (Direction dir : Direction.values()) {
			BlockPos offsetPos = pos.offset(dir);
			if (isOf(offsetPos, Blocks.REDSTONE_BLOCK))
				return offsetPos;

			if (hasEntity(offsetPos, entity -> entity.intersectionChecked))
				continue;
			if (!isReplaceable(offsetPos))
				continue;

			if (BlockUtils.canPlace(offsetPos))
				posList.add(offsetPos);
		}

		if (posList.isEmpty())
			return null;
		posList.sort(Comparator.comparingDouble(PositionMath::squareDistance));

		return posList.getFirst();
	}

	private BlockPos getPistonPos(BlockPos center) {
		if (mc.player == null || mc.world == null)
			return null;

		ArrayList<BlockPos> posList = new ArrayList<>();

		for (Direction dir : Direction.values()) {
			if (dir == Direction.DOWN || dir == Direction.UP)
				continue;

			BlockPos offsetPos = center.offset(dir);
			if (PositionMath.isOutOfRange(offsetPos, config.getPlaceRange().get()))
				continue;

			if (isOf(offsetPos, Blocks.PISTON, Blocks.STICKY_PISTON)) {
				if (!invalidPiston(offsetPos, center))
					return offsetPos;
			}

			if (hasEntity(offsetPos, entity -> entity.intersectionChecked))
				continue;
			if (!isReplaceable(offsetPos) && invalidPiston(offsetPos, center))
				continue;

			boolean
					canPush =
					isAir(center.up()) && isAir(center.offset(dir.getOpposite())) && isAir(center
							.offset(dir.getOpposite())
							.up());

			if (BlockUtils.canPlace(offsetPos) && canPush) {
				posList.add(offsetPos);
			}
		}

		if (posList.isEmpty())
			return null;
		int mul = config.getPushOnSelf().get() ? -1 : 1;
		posList.sort(Comparator.comparingDouble(position -> mul * PositionMath.squareDistance(position)));

		direction = directionFromBlock(center, posList.getFirst());
		return posList.getFirst();
	}

	private boolean invalidPiston(BlockPos pos, BlockPos center) {
		if (!isOf(pos, Blocks.PISTON, Blocks.STICKY_PISTON))
			return true;
		Direction validDirection = directionFromBlock(pos, center);
		Direction pistonDirection = getState(pos).get(Properties.FACING);
		return !validDirection.equals(pistonDirection);
	}
}
