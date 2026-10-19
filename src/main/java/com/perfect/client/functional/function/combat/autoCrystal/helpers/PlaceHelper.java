package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.PlaceInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.State;
import lombok.AllArgsConstructor;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static com.perfect.client.core.utils.game.world.BlockUtils.*;


public class PlaceHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	private static final double ANTI_HITBOX_DESYNC_OFFSET = 0.0005d;

	public PlaceHelper(AutoCrystal autoCrystal, AutoCrystalConfig config) {
		super(autoCrystal, config);
	}

	
	public void placeCrystal(PlaceInfo info) {
		if (mc.getNetworkHandler() == null
				|| mc.player == null
				|| mc.world == null
				|| info == null
				|| !info.getTarget().isAlive()
		) {
			return;
		}

		SwapInfo swapInfo = findSwap();

		if (swapInfo == null) {
			return;
		}

		if (info.isFacePlace() && needCancelFacePlace()) {
			return;
		}

		swapInfo.swap();

		Hand hand = swapInfo.isOffhand() ? Hand.OFF_HAND : Hand.MAIN_HAND;

		InteractUtils.simplePlace(info.getCrystalPlaceResult(), hand, config.getSwingHand().get());

		swapInfo.swapBack();

		function.render(info.getPos());
	}

	
	public SwapInfo findSwap() {
		SwapInfo swapInfo = SwapUtils.find(config.getSwap().get().convert(), Items.END_CRYSTAL);

		if (!swapInfo.isFound()) {
			return null;
		}

		if (config.getSwap().get(CrystalSwap.CC)) {
			if (swapInfo.isNeedSwap()) {
				return null;
			}
			if (!Managers.ANTI_CHEAT.isCcSwapDelayPassed()) {
				return null;
			}
		}

		return swapInfo;
	}

	private boolean needCancelFacePlace() {
		return config.getFacePlaceSlowness().get() == AutoCrystal.Slowness.PLACE
				&& !function.getTimerHelper().isFacePlaceTimerPassed();
	}

	public synchronized PlaceInfo getPlacePos(State state, boolean support) {
		if (mc.player == null || mc.world == null || state.getTargets().isEmpty()) {
			return null;
		}

		float bestDamage = -1;
		PlaceInfo bestPlaceInfo = null;

		boolean wasSurroundFacePlace = false;
		boolean wasNoSupport = false;
		boolean wasNoObstructive = false;

		List<BlockPos> cube = generateSortedPlaceSphere(state, support);

		if (state.getPlaceInfo() != null && cube.contains(state.getPlaceInfo().getPos())) {
			cube.remove(state.getPlaceInfo().getPos());
			cube.addFirst(state.getPlaceInfo().getPos());
		}

		for (BlockPos pos : cube) {
			if ((bestPlaceInfo != null && bestPlaceInfo.isSurround()) && !isSurroundPlace(state, pos)) {
				continue;
			}
			if (wasSurroundFacePlace && isOnlyFacePlace(state, pos) && !isSurroundFacePlace(state, pos)) {
				continue;
			}

			Intersection intersection = getEntityIntersection(state, pos, support);

			if (shouldSkipIntersection(state, intersection, wasNoObstructive)) {
				continue;
			}

			boolean isSupportPos = support && BlockUtils.isReplaceable(pos);
			if (isSupportPos && wasNoSupport) {
				continue;
			}

			BlockHitResult rayTracePos = getRayTracePos(pos);

			if (config.getRayTraceMode().get() != AutoCrystal.RayTraceMode.NONE && rayTracePos == null) {
				continue;
			}

			CrystalPlaceResult results = getCrystalResult(pos, rayTracePos, isSupportPos);
			if (results == null || results.crystalPlaceResult == null) {
				continue;
			}
			if (isSupportPos && results.supportResult == null) {
				continue;
			}

			Vec3d crystalVec3d = BlockUtils.getCrystalVec3d(pos);

			float bestPosDamage = -1;
			PlaceInfo bestPosInfo = null;

			for (LivingEntity entity : state.getTargets()) {
				if (function.getTargetHelper().isOnlyBreakCalculate(state, entity)) {
					continue;
				}

				PlaceInfo targetInfo = new PlaceInfo();

				targetInfo.setPos(pos);
				targetInfo.setObstructive(intersection.crystal);
				targetInfo.setRayTraceVec(rayTracePos != null ? rayTracePos.getPos() : null);
				targetInfo.setSurroundFacePlace(isSurroundFacePlace(entity, pos));

				if (wasSurroundFacePlace && isFacePlaceSimple(entity, pos) && !targetInfo.isSurroundFacePlace()) {
					continue;
				}
				if (targetInfo.isSurroundFacePlace()) {
					wasSurroundFacePlace = true;
				}

				Box targetBox = function.getTargetHelper().getEntityBox(entity);
				targetInfo.setTarget(entity);
				targetInfo.setSurround(isSurroundBlock(entity, pos));

				if ((bestPosInfo != null && bestPosInfo.isSurround()) && !targetInfo.isSurround()) {
					continue;
				}

				float minDamage = function.getDamageHelper().getMinDamageForTarget(entity);
				float preDamage = getDamage(entity, targetBox, crystalVec3d, false);

				if (preDamage < minDamage || preDamage <= bestPosDamage || preDamage <= bestDamage) {
					continue;
				}

				float damage = getDamage(entity, targetBox, crystalVec3d, true);

				if (damage < minDamage) {
					if (function.getTargetHelper().shouldFacePlace(entity)) {
						if (wasSurroundFacePlace && !targetInfo.isSurroundFacePlace()) {
							continue;
						}
						targetInfo.setFacePlace(true);
						if (damage < AutoCrystal.MIN_FACE_PLACE_DAMAGE) {
							continue;
						}
					}
					else {
						continue;
					}
				}

				if (damage > bestPosDamage) {
					if (intersection.crystal == null) {
						wasNoObstructive = true;
					}
					if (support && !wasNoSupport && !BlockUtils.isReplaceable(pos)) {
						wasNoSupport = true;
					}
					bestPosDamage = damage;
					bestPosInfo = targetInfo;
				}
			}

			if (bestPosInfo != null && bestPosDamage > bestDamage && isSafePlace(bestPosDamage, pos)) {
				bestPlaceInfo = bestPosInfo;
				bestDamage = bestPosDamage;

				bestPlaceInfo.setCrystalPlaceResult(results.crystalPlaceResult);
				bestPlaceInfo.setSupportResult(results.supportResult);
			}
		}

		return bestPlaceInfo;
	}

	
	private float getDamage(LivingEntity entity, Box entityBox, Vec3d crystalPos, boolean exposure) {
		return function.getDamageHelper().getDamage(entity, entityBox, crystalPos, true, exposure);
	}

	
	public List<BlockPos> generateSortedPlaceSphere(State state, boolean support) {
		if (mc.player == null) {
			return new ArrayList<>();
		}
		int range = config.getPlaceRange().get().intValue() + 1;

		List<BlockPos> cube = PositionMath.getCube(BlockPos.ofFloored(mc.player.getEyePos()), range, range, pos -> {
			if (isInvalidBlock(pos, support)) {
				return false;
			}
			if (function.getTargetHelper().isWithoutDamage(state, pos, true)) {
				return false;
			}
			if (isOutOfRange(pos)) {
				return false;
			}

			return true;
			//return !isOnlyFacePlace(state, pos) || hasFacePlace(state);
		});

		if (cube.isEmpty()) {
			return cube;
		}

		cube.sort(Comparator.comparing(pos -> {
			double best = -1;
			for (Entity entity : state.getTargets()) {
				double distance = PositionMath.squareDistance(entity.getPos(), Vec3d.ofBottomCenter(pos.up()));
				if (distance < best || best == -1) {
					best = distance;
				}
			}
			return best;
		}));

		return cube;
	}

	
	private boolean isFacePlaceSimple(LivingEntity entity, BlockPos pos) {
		return pos.getY() >= entity.getBlockPos().getY();
	}

	
	private boolean isOnlyFacePlace(State state, BlockPos pos) {
		for (LivingEntity entity : state.getTargets()) {
			if (!isFacePlaceSimple(entity, pos) || !function.getTargetHelper().isRegularTarget(state, entity)) {
				return false;
			}
		}
		return true;
	}

	
	private boolean hasFacePlace(State state) {
		for (LivingEntity entity : state.getTargets()) {
			if (function.getTargetHelper().shouldFacePlace(entity)) {
				return true;
			}
		}
		return false;
	}

	
	private boolean isSurroundFacePlace(State state, BlockPos pos) {
		for (LivingEntity entity : state.getTargets()) {
			if (isSurroundFacePlace(entity, pos)) {
				return true;
			}
		}
		return false;
	}

	
	private boolean isSurroundFacePlace(LivingEntity entity, BlockPos pos) {
		return TrapUtils.simpleSurround(entity.getBlockPos()).contains(pos);
	}

	
	private boolean isSurroundPlace(State state, BlockPos pos) {
		for (LivingEntity entity : state.getTargets()) {
			if (isSurroundBlock(entity, pos)) {
				return true;
			}
		}
		return false;
	}

	
	private boolean isSurroundBlock(LivingEntity entity, BlockPos pos) {
		return TrapUtils.simpleSurround(entity.getBlockPos().down()).contains(pos);
	}

	
	private Intersection getEntityIntersection(State state, BlockPos pos, boolean support) {
		if (support) {
			Intersection intersection = getIntersectionForBlock(pos);

			if (intersection.found) {
				return intersection;
			}
		}

		BlockPos crystalBlockPos = pos.up();

		Box hitboxForCrystals = getHitboxForCrystals(crystalBlockPos);
		Box hitboxForEntities = getHitboxForEntities(crystalBlockPos);

		Vec3d crystalVec3d = BlockUtils.getCrystalVec3d(pos);

		return getIntersectionForCrystal(state, crystalVec3d, hitboxForCrystals, hitboxForEntities);
	}

	private Intersection getIntersectionForCrystal(
			State state,
			Vec3d crystalVec3d,
			Box hitboxForCrystals,
			Box hitboxForEntities
	) {
		Supplier<Boolean> entitySupplier = () -> BlockUtils.hasEntity(
				hitboxForEntities,
				e -> isIntersectEntity(state, e, crystalVec3d, false)
		);

		Supplier<List<Entity>> crystalSupplier = () -> BlockUtils.getEntities(
				hitboxForCrystals,
				entity -> isIntersectEntity(state, entity, crystalVec3d, true)
		);

		return getIntersection(entitySupplier, crystalSupplier);
	}

	
	private Intersection getIntersectionForBlock(BlockPos pos) {
		Supplier<Boolean> entitySupplier = () -> BlockUtils.hasEntity(
				pos,
				entity -> !(entity instanceof EndCrystalEntity)
		);

		Supplier<List<Entity>> crystalSupplier = () -> BlockUtils.getEntities(
				pos,
				entity -> entity instanceof EndCrystalEntity
		);

		return getIntersection(entitySupplier, crystalSupplier);
	}

	
	private Intersection getIntersection(
			Supplier<Boolean> entitySupplier,
			Supplier<List<Entity>> crystalSupplier
	) {
		if (entitySupplier.get()) {
			return new Intersection(true, null);
		}
		else {
			List<Entity> crystals = crystalSupplier.get();

			if (!crystals.isEmpty() && crystals.getFirst() instanceof EndCrystalEntity crystal) {
				return new Intersection(true, crystal);
			}
			else {
				return new Intersection(false, null);
			}
		}
	}

	
	private boolean isIntersectEntity(State state, Entity entity, Vec3d crystalVec3d, boolean crystals) {
		if (entity == null) {
			return false;
		}
		if (Managers.CRYSTAL.isBlownUp(entity)) {
			return false;
		}
		if (willBlown(state, entity)) {
			return false;
		}

		if (crystals) {
			return isIntersectedCrystal(state, entity, crystalVec3d);
		}
		else {
			return !(entity instanceof EndCrystalEntity);
		}
	}

	
	private boolean isIntersectedCrystal(State state, Entity entity, Vec3d validVec3d) {
		if (!(entity instanceof EndCrystalEntity)) {
			return false;
		}
		if (state.getBreakInfo() != null && entity.equals(state.getBreakInfo().getCrystal())) {
			return false;
		}
		return !entity.getPos().equals(validVec3d);
	}

	
	public Box getHitboxForCrystals(BlockPos pos) {
		Box box = new Box(pos);
		return box.expand(0.5, 0.5, 0.5).stretch(0, 0.5, 0);
	}

	
	private Box getHitboxForEntities(BlockPos pos) {
		Box box = new Box(pos);

		if (!config.getLowerHitbox().get()) {
			box = box.stretch(0, 0.5, 0);
		}
		if (config.getAntiHitboxDesync().get()) {
			box = box.expand(ANTI_HITBOX_DESYNC_OFFSET, 0, ANTI_HITBOX_DESYNC_OFFSET);
		}

		return box;
	}

	
	private boolean isOutOfRange(BlockPos pos) {
		assert mc.player != null;

		if (BlockUtils.isOutOfBorder(pos)) {
			return true;
		}

		RaytraceData placePos = hasLineOfSight(mc.player.getEyePos(), pos.toCenterPos(), pos);
		RaytraceData crystalPos = hasLineOfSight(mc.player.getEyePos(), pos.toCenterPos().add(0, 0.5, 0), pos);

		boolean raycast = !placePos.found
				&& !crystalPos.found;

		if (config.getWallsBreakRange().isEnabled() && raycast) {
			return PositionMath.isOutOfRange(pos, config.getWallsBreakRange().get());
		}

		if (PositionMath.isOutOfRange(BlockUtils.getCrystalVec3d(pos), config.getBreakRange().get())) {
			return true;
		}

		if (config.getWallsPlaceRange().isEnabled() && raycast) {
			return PositionMath.isOutOfRange(pos, config.getWallsPlaceRange().get());
		}

		return PositionMath.isOutOfRange(pos, config.getPlaceRange().get());
	}

	
	private boolean isSafePlace(float targetDamage, BlockPos pos) {
		if (mc.player == null) {
			return false;
		}

		EndCrystalEntity crystal = new EndCrystalEntity(
				mc.world,
				pos.getX() + 0.5,
				pos.getY() + 1,
				pos.getZ() + 0.5
		);

		if (function.getTargetHelper().isWithoutDamage(mc.player, pos, true)) {
			return true;
		}

		float preSelfDamage = function.getDamageHelper().getSelfDamage(crystal.getPos(), true, true);

		if (function.getTargetHelper().isSafeDamage(targetDamage, preSelfDamage)) {
			return true;
		}

		float selfDamage = function.getDamageHelper().getSelfDamage(crystal.getPos(), true, true);
		return function.getTargetHelper().isSafeDamage(targetDamage, selfDamage);
	}

	
	public boolean isInvalidBlock(BlockPos pos, boolean support) {
		if (!isOf(pos, Blocks.BEDROCK, Blocks.OBSIDIAN)) {
			if (!(support && isReplaceable(pos))) {
				return true;
			}
		}
		if (!isAir(pos.up())) {
			return true;
		}
		if (config.getLegacy().get() && !isAir(pos.up(2))) {
			return true;
		}

		return false;
	}

	
	private BlockHitResult getRayTracePos(BlockPos pos) {
		if (mc.player == null || mc.world == null) return null;

		Vec3d eyes = mc.player.getEyePos();
		AutoCrystal.RayTraceMode mode = config.getRayTraceMode().get();

		// У меня есть сомнения, что целочисленная координата угла позволит нормально плейсить блоки без ругательств античита
		double offset = 0.03;

		if (mode == AutoCrystal.RayTraceMode.STRICT) {
			double closestX = Math.max(pos.getX() + offset, Math.min(pos.getX() + 1.0 - offset, eyes.x));
			double closestZ = Math.max(pos.getZ() + offset, Math.min(pos.getZ() + 1.0 - offset, eyes.z));

			Vec3d target = new Vec3d(closestX, pos.getY() + 1.0, closestZ);
			RaytraceData raytraceData = hasLineOfSight(eyes, target, pos);

			if (raytraceData.found) {
				return raytraceData.blockHitResult;
			}
		}
		else if (mode == AutoCrystal.RayTraceMode.CENTER) {
			Vec3d centerTop = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
			RaytraceData raytraceData = hasLineOfSight(eyes, centerTop, pos);

			if (raytraceData.found) {
				return raytraceData.blockHitResult;
			}
		}
		else if (mode == AutoCrystal.RayTraceMode.EDGES) {
			double[] searchCoords = {offset, 0.5, 1.0 - offset};

			for (double x : searchCoords) {
				for (double y : searchCoords) {
					for (double z : searchCoords) {
						Vec3d edgePos = new Vec3d(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
						RaytraceData raytraceData = hasLineOfSight(eyes, edgePos, pos);

						if (raytraceData.found) {
							return raytraceData.blockHitResult;
						}
					}
				}
			}
		}

		return null;
	}

	
	public RaytraceData hasLineOfSight(Vec3d start, Vec3d end, BlockPos target) {
		assert mc.world != null;
        assert mc.player != null;

        RaycastContext context = new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				mc.player
		);

        BlockHitResult result = mc.world.raycast(context);

		return new RaytraceData(result.getBlockPos().equals(target), result);
	}

	
	private boolean willBlown(State state, Entity entity) {
		if (state.getBreakInfo() == null) {
			return false;
		}
		if (entity instanceof EndCrystalEntity) {
			return PositionMath.maxSimpleDistance(
					state.getBreakInfo().getCrystal().getBlockPos(),
					entity.getBlockPos()
			) <= 5;
		}
		return entity instanceof ItemEntity
				&& PositionMath.simpleDistance(state.getBreakInfo().getCrystal().getBlockPos(), entity.getBlockPos())
				<= 1;
	}

	private boolean shouldSkipIntersection(
			State state,
			Intersection intersection,
			boolean wasWithoutObstructiveBefore
	) {
		if (!intersection.found) {
			return false;
		}
		if (intersection.crystal != null) {
			return wasWithoutObstructiveBefore || function.getBreakHelper().isInvalidBreak(state, intersection.crystal);
		}
		return true;
	}

	
	public static CrystalPlaceResult getCrystalResult(BlockPos pos, BlockHitResult rayTracePos, boolean support) {
		CrystalPlaceResult result = new CrystalPlaceResult();

		if (support) {
			result.supportResult = HitResultUtils.getPlaceResult(pos);
			if (result.supportResult == null) {
				return null;
			}
		}

		result.crystalPlaceResult = HitResultUtils.getInteractResult(pos, rayTracePos);

		return result;
	}

	@AllArgsConstructor
	public static class Intersection {

		public boolean found;
		public EndCrystalEntity crystal;
	}

	public static class CrystalPlaceResult {

		public BlockHitResult crystalPlaceResult;
		public BlockHitResult supportResult;
	}

	@AllArgsConstructor
	public static class RaytraceData {

		public boolean found;
		public BlockHitResult blockHitResult;
	}
}