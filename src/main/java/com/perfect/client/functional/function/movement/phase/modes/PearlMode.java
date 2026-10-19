package com.perfect.client.functional.function.movement.phase.modes;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.movement.phase.Phase;
import com.perfect.client.functional.function.movement.phase.PhaseConfig;
import com.perfect.client.functional.function.movement.phase.utils.PhaseFindResult;
import com.perfect.client.functional.function.movement.phase.utils.PhaseMode;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PearlMode extends PhaseMode {

	private static final double SECONDS_TO_DISABLE = 5d;

	private static final double FIRE_INTERACT_OFFSET = 0.01193;

	private static final double PEARL_SIZE = 0.13;

	private static final double REGULAR_RANGE = 1f;
	private static final double NOT_FULL_BLOCK_RANGE = 0.8f;
	private static final double DIAGONAL_RANGE = 1.2f;
	private static final double IF_CRYSTAL_RANGE = 0.49f;

	private static final double MAX_MOVEMENT = 9f;

	private PhaseFindResult phaseResult;
	private SwapInfo swapInfo;

	private float rotateYaw;
	private float rotatePitch;

	public PearlMode(Phase phase, PhaseConfig config) {
		super(phase, config);
	}

	
	@Override
	public void onPhaseRotate() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (shouldDisable()) {
			function.disable();
			return;
		}

		if (mc.player.getItemCooldownManager().isCoolingDown(Items.ENDER_PEARL.getDefaultStack())) {
			return;
		}

		swapInfo = findPearl();

		if (!swapInfo.isFound()) {
			function.error("No pearl found");
			function.disable();
			return;
		}

		phaseResult = getPhaseResult();

		if (phaseResult == null) {
			return;
		}

		rotateYaw = RotationUtils.getYaw(phaseResult.vec3d());
		rotatePitch = RotationUtils.getPitch(phaseResult.vec3d());

		switch (config.getRotate().get()) {
			case INSTANT -> {
				PacketUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
						rotateYaw,
						rotatePitch,
						mc.player.isOnGround(),
						mc.player.horizontalCollision
				));

				usePearl(swapInfo, rotateYaw, rotatePitch);
			}
			case VANILLA -> {
				Managers.ROTATION.setRotateYaw(rotateYaw);
				Managers.ROTATION.setRotatePitch(rotatePitch);
			}
		}
	}

	
	@Override
	public void onPostSendMovement() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (!config.getRotate().is(RotateMode.VANILLA)) {
			return;
		}

		if (shouldDisable()) {
			function.disable();
			return;
		}

		if (phaseResult == null || swapInfo == null) {
			return;
		}

		if (isRotated()) {
			usePearl(swapInfo, Managers.ROTATION.getSentYaw(), Managers.ROTATION.getSentPitch());
		}
	}

	public boolean shouldDisable() {
		return PlayerUtils.isPhased()
				|| PlayerUtils.isShifting()
				|| PlayerUtils.isCrawling();
	}

	private boolean isRotated() {
		float yawDelta = Math.abs(Managers.ROTATION.getSentYaw() - rotateYaw);
		float pitchDelta = Math.abs(Managers.ROTATION.getSentPitch() - rotatePitch);

		return yawDelta <= 1f && pitchDelta <= 1f;
	}

	private void antiPearlRotate() {
		if (mc.player == null || !config.getAntiPearlRotate().get()) {
			return;
		}

		PacketUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
				mc.player.getYaw(),
				mc.player.getPitch(),
				mc.player.isOnGround(),
				mc.player.horizontalCollision
		));
	}

	
	private void usePearl(SwapInfo swapInfo, float yaw, float pitch) {
		useBypass();
		useAntiObstructive();

		swapInfo.swap();

		InvUtils.useItem(swapInfo.getHand(), yaw, pitch);

		swapInfo.swapBack();

		antiPearlRotate();

		function.disable();
	}

	private void useAntiObstructive() {
		if (phaseResult == null) {
			return;
		}

		if (config.getAntiScaffolding().get()) {
			antiScaffolding();
		}
		if (config.getAntiEntity().get()) {
			antiEntity();
		}
	}

	
	private void useBypass() {
		if (mc.player == null) {
			return;
		}

		if (phaseResult == null) {
			return;
		}

		if (config.getBypass().is(Bypass.NONE)) {
			return;
		}

		BlockPos playerPos = mc.player.getBlockPos();

		if (!BlockUtils.isReplaceable(playerPos) || BlockUtils.isOf(playerPos, Blocks.COBWEB, Blocks.FIRE)) {
			return;
		}

		Item item = switch (config.getBypass().get()) {
			case COBWEB -> Items.COBWEB;
			case FLINT -> Items.FLINT_AND_STEEL;
			case FIREBALL -> Items.FIRE_CHARGE;
			case NONE -> null;
		};

		SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), item);

		if (!swapInfo.isFound()) {
			return;
		}

		switch (config.getBypass().get()) {
			case COBWEB -> placeCobweb(swapInfo, playerPos);
			case FLINT, FIREBALL -> placeFire(swapInfo, playerPos);
		}

		swapInfo.swapBack();
	}

	private void placeFire(SwapInfo swapInfo, BlockPos playerPos) {
		BlockPos pos = playerPos.down();
		Vec3d vec3d = PositionMath.closestVec3d(phaseResult.vec3d(), new Box(pos).expand(-FIRE_INTERACT_OFFSET));

		BlockHitResult hitResult = new BlockHitResult(vec3d, Direction.UP, pos, false);

		InteractUtils.placeBlockIternal(hitResult, swapInfo.getResult(), swapInfo.getPlaceArgs());
	}

	private void placeCobweb(SwapInfo swapInfo, BlockPos playerPos) {
		InteractUtils.placeBlock(
				playerPos,
				swapInfo.getResult(),
				Rotate.NONE,
				swapInfo.getPlaceArgs()
		);
	}

	private void antiScaffolding() {
		if (mc.player == null) {
			return;
		}

		BlockPos pos = mc.player.getBlockPos();

		if (BlockUtils.isOf(pos, Blocks.SCAFFOLDING)) {
			sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
		}
	}

	private void antiEntity() {
		if (mc.player == null) {
			return;
		}

		List<Entity> entities = BlockUtils.getEntities(
				getPearlBox(phaseResult.vec3d()),
				entity -> entity instanceof EndCrystalEntity || entity instanceof ItemFrameEntity
		);

		for (Entity entity : entities) {
			if (isObstructiveEntity(entity)) {

				if (entity instanceof ItemFrameEntity frame && !frame.getHeldItemStack().isEmpty()) {
					attackEntity(entity);
				}

				attackEntity(entity);
			}
		}
	}

	private Box getPearlBox(Vec3d vec3d) {
		return PositionMath.boxFromCenter(PEARL_SIZE, vec3d);
	}

	private void attackEntity(Entity entity) {
		if (mc.player == null) {
			return;
		}

		sendPacket(PlayerInteractEntityC2SPacket.attack(entity, mc.player.isSneaking()));

		sendPacket(new HandSwingC2SPacket(
				mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND
		));
	}

	private boolean isObstructiveEntity(Entity entity) {
		if (mc.player == null) {
			return false;
		}

		return !entity.getBoundingBox().contains(mc.player.getEyePos());
	}

	public PhaseFindResult getPhaseResult() {
		if (mc.player == null) {
			return null;
		}

		BlockPos pos = getPhaseBlock();

		if (pos == null) {
			return null;
		}

		VoxelShape shape = BlockUtils.getOutlineShape(pos);

		if (shape.isEmpty()) {
			return null;
		}

		Vec3d vec3d = PositionMath.closestVec3d(
				mc.player.getPos().add(0, 0, 0),
				shape.getBoundingBox().offset(pos)
		).add(0, getMovementOffset(), 0);

		return new PhaseFindResult(pos, vec3d);
	}

	private double getMovementOffset() {
		double movement = PlayerUtils.getSpeedInBPS().horizontalLength();
		if (movement == 0) {
			return 0;
		}
		double factor = Math.clamp(movement / MAX_MOVEMENT, 0, 1);
		double value = MathUtils.interpolate(0, 1, factor);

		if (config.getRotate().is(RotateMode.VANILLA)) {
			return -value;
		}
		else {
			return value;
		}
	}

	private BlockPos getPhaseBlock() {
		List<BlockPos> possibleBlocks = getPossibleBlocks();

		if (possibleBlocks.isEmpty()) {
			return null;
		}

		sortPositions(possibleBlocks);

		if (possibleBlocks.isEmpty()) {
			return null;
		}

		return possibleBlocks.getFirst();
	}

	private void sortPositions(List<BlockPos> list) {
		if (config.getSmart().get()) {
			Utils.keepHigherValues(list, pos -> (double) getBlockScore(list, pos));
		}
		//Utils.keepLowerValues(list, this::getSquareDistance);
		Utils.keepHigherValues(list, pos -> -getSquareDistance(pos));
	}

	private double getSquareDistance(BlockPos pos) {
		if (mc.player == null) {
			return -1;
		}

		return PositionMath.squareDistance(mc.player.getPos(), PositionMath.closestVec3d(mc.player.getPos(), pos));
	}

	private int getBlockScore(List<BlockPos> possibleBlocks, BlockPos pos) {
		if (mc.player == null) {
			return -1;
		}

		int score = 0;

		if (BlockUtils.isBlastResist(pos)) {
			score += 10;

			if (!BlockUtils.isBreakable(pos)) {
				score += 5;
			}
		}

		if (BlockUtils.getState(pos).isFullCube(mc.world, pos)) {
			score += 2;
		}

		if (isDiagonal(mc.player.getBlockPos(), pos) && possibleBlocks != null) {

			for (BlockPos possible : possibleBlocks) {
				if (isNeighbor(pos, possible)) {
					score += getBlockScore(null, possible);
				}
			}

		}

		return score;
	}

	private boolean isNeighbor(BlockPos pos1, BlockPos pos2) {
		return PositionMath.simpleDistance(pos1, pos2) == 1;
	}

	private List<BlockPos> getPossibleBlocks() {
		if (mc.player == null) {
			return Collections.emptyList();
		}

		BlockPos center = mc.player.getBlockPos();
		List<BlockPos> cube = PositionMath.getCube(center, 1, 0);

		List<BlockPos> list = new ArrayList<>();

		for (BlockPos pos : cube) {
			VoxelShape shape = BlockUtils.getOutlineShape(pos);

			if (shape.isEmpty()) {
				continue;
			}

			if (isGoodBlock(center, pos, shape)) {
				list.add(pos);
			}
		}

		return list;
	}

	private boolean isGoodBlock(BlockPos center, BlockPos pos, VoxelShape shape) {
		if (BlockUtils.isReplaceable(pos)
				|| !BlockUtils.isCollidable(pos)
				|| BlockUtils.isOf(pos, Blocks.BARRIER)) {
			return false;
		}

		if (!checkFloor(pos, shape)) {
			return false;
		}

		if (!checkShape(shape)) {
			return false;
		}

		if (!checkDiagonal(center, pos)) {
			return false;
		}

		return checkRange(pos, shape);
	}

	private boolean checkRange(BlockPos pos, VoxelShape shape) {
		if (mc.player == null) {
			return false;
		}

		double range = getRangeForBlock(pos);
		Vec3d vec3d = PositionMath.closestVec3d(mc.player.getPos(), shape.getBoundingBox().offset(pos));

		return isInRange(vec3d, range);
	}

	private double getRangeForBlock(BlockPos pos) {
		if (BlockUtils.hasEntity(pos.up(), this::isCrystal)) {
			return IF_CRYSTAL_RANGE;
		}

		if (BlockUtils.getState(pos).isFullCube(mc.world, pos)) {
			return REGULAR_RANGE;
		}
		else {
			return NOT_FULL_BLOCK_RANGE;
		}
	}

	private boolean isCrystal(Entity entity) {
		return entity instanceof EndCrystalEntity && isFriendlyCrystal(entity);
	}

	private boolean isFriendlyCrystal(Entity entity) {
		if (mc.player == null) {
			return false;
		}
		return entity.getBoundingBox().maxY > mc.player.getEyePos().y;
	}

	private boolean checkDiagonal(BlockPos center, BlockPos pos) {

		if (!isDiagonal(center, pos)) {
			return true;
		}

		if (!BlockUtils.getState(pos).isFullCube(mc.world, pos)) {
			return false;
		}

		return isInDiagonalRange(pos);
	}

	private boolean isInDiagonalRange(BlockPos pos) {
		if (mc.player == null) {
			return true;
		}

		return isInRange(PositionMath.closestVec3d(mc.player.getPos(), pos), PearlMode.DIAGONAL_RANGE);
	}

	private boolean isInRange(Vec3d pos, double range) {
		if (mc.player == null) {
			return true;
		}

		return !PositionMath.isOutOfRange(mc.player.getPos(), pos, range);
	}

	private boolean isDiagonal(BlockPos center, BlockPos pos) {
		return PositionMath.simpleDistance(center, pos) > 1;
	}

	private boolean checkShape(VoxelShape shape) {
		// Если число боксов больше 1, то остальные боксы мешают залезть в блок.
		return shape.getBoundingBoxes().size() == 1;
	}

	private boolean checkFloor(BlockPos pos, VoxelShape shape) {
		BlockPos floor = pos.down();

		if (!BlockUtils.isCollidable(floor)) {
			return false;
		}

		return shape.getBoundingBox().maxY >= 1;
	}

	private SwapInfo findPearl() {
		return SwapUtils.find(config.getSwap().get(), Items.ENDER_PEARL);
	}

	public enum Bypass {
		NONE,
		COBWEB,
		FLINT,
		FIREBALL
	}

	public enum RotateMode {
		VANILLA,
		INSTANT
	}
}