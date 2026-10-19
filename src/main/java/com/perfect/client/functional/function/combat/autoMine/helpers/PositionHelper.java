package com.perfect.client.functional.function.combat.autoMine.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.interact.SupportUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.autoMine.AutoMine;
import com.perfect.client.functional.function.combat.autoMine.AutoMineConfig;
import com.perfect.client.functional.function.combat.autoMine.data.BlockCheckResult;
import com.perfect.client.functional.function.combat.autoMine.data.PositionInfo;
import com.perfect.client.functional.function.combat.autoMine.data.TrapInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.perfect.client.core.utils.game.world.BlockUtils.getCrystalVec3d;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;


public class PositionHelper extends FunctionHelper<AutoMine, AutoMineConfig> {

	public PositionHelper(AutoMine autoMine, AutoMineConfig config) {
		super(autoMine, config);
	}

	private static void sort(List<PositionInfo> positions) {
		Helper.sortByProperty(positions, AutoMine.SortProperty.PRIORITY);
		Helper.sortByProperty(positions, AutoMine.SortProperty.DAMAGE);
	}

	private static PositionInfo getBestPosition(List<PositionInfo> positions) {
		if (positions.isEmpty()) {
			return new PositionInfo();
		}

		sort(positions);

		return positions.getFirst();
	}

	public PositionInfo getPosition(BlockPos excluded, boolean support) {
		if (mc.world == null || mc.player == null) {
			return new PositionInfo();
		}

		List<PositionInfo> possiblePositions = new ArrayList<>();

		long ms = System.currentTimeMillis();

		for (Entity entity : function
				.getHelper()
				.getTargets(new Box(BlockPos.ofFloored(EntityUtils.getEyePos(mc.player))).expand(
						config.getRange().get() + 1))) {
			if (System.currentTimeMillis() - ms >= 10) {
				break;
			}
			if (!(entity instanceof PlayerEntity target)) {
				continue;
			}
			possiblePositions.add(getPositionByTarget(excluded, target, support));
		}

		possiblePositions.removeIf(position -> !position.found() || position.getMinePosition().equals(excluded));

		if (possiblePositions.isEmpty()) {
			return new PositionInfo();
		}

		sort(possiblePositions);

		return possiblePositions.getFirst();
	}

	public PositionInfo getPositionByTarget(BlockPos excluded, PlayerEntity target, boolean support) {
		List<BlockPos> blocks = new ArrayList<>();

		TrapInfo trapInfo = new TrapInfo();
		trapInfo.fill(target);

		if (config.getPositions().isEnabled(AutoMineType.CITY)
				|| config.getPositions().isEnabled(AutoMineType.SURROUND_CEV)) {
			blocks.addAll(trapInfo.getSurround());
		}
		if (config.getPositions().isEnabled(AutoMineType.BURROW)) {
			blocks.addAll(trapInfo.getBurrow());
		}
		if (config.getPositions().isEnabled(AutoMineType.TRAP_CEV)) {
			blocks.addAll(trapInfo.getTrap());
		}
		if (config.getPositions().isEnabled(AutoMineType.FAR_CEV)) {
			ArrayList<BlockPos> cube = PositionMath.getCube(target.getEyePos().add(0, 1, 0), 3, 1);
			cube.removeAll(blocks);
			blocks.addAll(cube);
		}

		List<PositionInfo> possiblePositions = new ArrayList<>();

		double bestDamage = 0;
		float bestPriority = 0;

		long ms = System.currentTimeMillis();

		for (BlockPos pos : blocks) {
			if (System.currentTimeMillis() - ms >= 10) {
				break;
			}
			if (pos.equals(excluded)) {
				continue;
			}

			PositionInfo result = getPositionByBlock(target, trapInfo, bestDamage, bestPriority, pos, support);

			if (!result.found()) {
				continue;
			}

			if (result.getPriority() >= bestPriority) {
				if (result.getDamage() > bestDamage || result.getPriority() > bestPriority) {
					bestDamage = result.getDamage();
					possiblePositions.add(result);
					bestPriority = result.getPriority();
				}
			}
		}

		return getBestPosition(possiblePositions);
	}

	public PositionInfo getPositionByBlock(BlockPos pos, boolean support) {
		if (mc.world == null) {
			return new PositionInfo();
		}

		List<PositionInfo> possiblePositions = new ArrayList<>();

		double bestDamage = 0;
		float bestPriority = 0;

		for (Entity entity : function.getHelper().getTargets(new Box(pos).expand(3))) {
			if (!(entity instanceof PlayerEntity target)) {
				continue;
			}
			TrapInfo trapInfo = new TrapInfo();
			trapInfo.fill(target);

			PositionInfo result = getPositionByBlock(target, trapInfo, bestDamage, bestPriority, pos, support);

			if (!result.found()) {
				continue;
			}

			if (result.getPriority() >= bestPriority) {
				if (result.getDamage() > bestDamage || result.getPriority() > bestPriority) {
					bestDamage = result.getDamage();
					possiblePositions.add(result);
					bestPriority = result.getPriority();
				}
			}
		}

		return getBestPosition(possiblePositions);
	}

	public PositionInfo getPositionByBlock(
			PlayerEntity target,
			TrapInfo trapInfo,
			double bestDamage,
			float bestPriority,
			BlockPos minePos,
			boolean support
	) {
		if (mc.player == null || mc.world == null) {
			return new PositionInfo();
		}
		if (PositionMath.isOutOfRange(
				PositionMath.closestVec3d(mc.player.getEyePos(), minePos),
				config.getRange().get()
		)) {
			return new PositionInfo();
		}

		List<AutoMineType> types = Arrays.asList(AutoMineType.values());
		types.sort(Comparator.comparing(type -> config.getPositions().getOrder(type)));

		for (AutoMineType type : types) {
			switch (type) {
				case BURROW -> {
					if (config.getPositions().isEnabled(AutoMineType.BURROW)
							&& trapInfo.getBurrow().contains(minePos)) {
						List<PositionInfo> positions = collectPositionListByBlock(
								target,
								trapInfo,
								bestDamage,
								bestPriority,
								minePos,
								AutoMineType.BURROW,
								support,
								false,
								Direction.UP,
								Direction.DOWN
						).result();

						if (!positions.isEmpty()) {
							return getBestPosition(positions);
						}
					}
				}
				case CITY -> {
					if (trapInfo.getSurround().contains(minePos)
							&& config.getPositions().isEnabled(AutoMineType.CITY)) {
						List<PositionInfo> positions = collectPositionListByBlock(
								target,
								trapInfo,
								bestDamage,
								bestPriority,
								minePos,
								AutoMineType.CITY,
								support,
								false,
								Direction.UP
						).result();

						if (!positions.isEmpty()) {
							return getBestPosition(positions);
						}
					}
				}
				case SURROUND_CEV -> {
					if (trapInfo.getSurround().contains(minePos)
							&& config.getPositions().isEnabled(AutoMineType.SURROUND_CEV)) {
						List<PositionInfo> positions = collectPositionListByBlock(
								target,
								trapInfo,
								bestDamage,
								bestPriority,
								minePos,
								AutoMineType.SURROUND_CEV,
								support,
								true,
								Direction.UP
						).result();

						if (!positions.isEmpty()) {
							return getBestPosition(positions);
						}
					}
				}
				case TRAP_CEV -> {
					if (trapInfo.getTrap().contains(minePos)
							&& config.getPositions().isEnabled(AutoMineType.TRAP_CEV)) {
						List<PositionInfo> positions = collectPositionListByBlock(
								target,
								trapInfo,
								bestDamage,
								bestPriority,
								minePos,
								AutoMineType.TRAP_CEV,
								support,
								true,
								Direction.UP
						).result();

						if (!positions.isEmpty()) {
							return getBestPosition(positions);
						}
					}
				}
				case FAR_CEV -> {
					if (config.getPositions().isEnabled(AutoMineType.FAR_CEV)
							&& minePos.getY() >= BlockPos.ofFloored(target.getEyePos()).getY()) {
						List<PositionInfo> positions = collectPositionListByBlock(
								target,
								trapInfo,
								bestDamage,
								bestPriority,
								minePos,
								AutoMineType.FAR_CEV,
								support,
								true,
								Direction.UP
						).result();

						if (!positions.isEmpty()) {
							return getBestPosition(positions);
						}
					}
				}
			}
		}

		return new PositionInfo();
	}

	private CollectedPositions collectPositionListByBlock(
			PlayerEntity target,
			TrapInfo trapInfo,
			double bestDamage,
			float bestPriority,
			BlockPos minePos,
			AutoMineType type,
			boolean support,
			boolean whiteListDirections,
			Direction... directions
	) {
		if (mc.player == null || mc.world == null)
			return CollectedPositions.empty();
		if (isReplaceable(minePos) && (!Helper.isCev(type) || !trapInfo.isSurrounded()))
			return CollectedPositions.empty();
		if (Helper.isCev(type) && !trapInfo.isSurrounded())
			return CollectedPositions.empty();
		if (type == AutoMineType.FAR_CEV && trapInfo.getTrap().contains(minePos)) {
			return CollectedPositions.empty();
		}

		List<PositionInfo> positions = new ArrayList<>();
		float priority = config.getPositions().getPriority(type);

		float _bestBlockDamage = 0;

		for (Direction direction : Direction.values()) {
			boolean contains = Arrays.asList(directions).contains(direction);
			if (whiteListDirections && !contains)
				continue;
			if (!whiteListDirections && contains)
				continue;

			BlockPos offsetPos = minePos.offset(direction);
			BlockPos crystalPlacePos = offsetPos.down();

			if (PositionMath.isOutOfRange(
					PositionMath.closestVec3d(mc.player.getEyePos(), crystalPlacePos),
					config.getRange().get()
			)) {
				continue;
			}

			BlockCheckResult blockCheck = function.getHelper().isValidPlace(
					type,
					minePos,
					crystalPlacePos,
					support && type != AutoMineType.BURROW
			);
			if (blockCheck.isInvalid()) {
				continue;
			}

			if (Helper.isCev(type)) {
				if (cevRaycast(target, crystalPlacePos)) {
					continue;
				}

				float preDamage = getDamage(
						target,
						minePos,
						crystalPlacePos,
						DamageCheck.PRE,
						config.getMinCevDamage().get(),
						null,
						type
				);

				if (preDamage == 0) {
					continue;
				}

				if (preDamage < bestDamage && priority == bestPriority) {
					continue;
				}
				if (preDamage < _bestBlockDamage) {
					continue;
				}
			}

			List<BlockPos> supportPositions = new ArrayList<>();

			if (type != AutoMineType.SURROUND_CEV && isReplaceable(crystalPlacePos)) {
				supportPositions.add(crystalPlacePos);
			}

			BlockPos additionalSupport = type.additionalSupport(minePos);
			if (additionalSupport != null && isReplaceable(additionalSupport))
				supportPositions.add(additionalSupport);

			SupportUtils.addSupport(supportPositions, config.getSupport().get(), config.getSupportLength().get());
			if (blockCheck.canPlaceCrystal() && isUnsafePosition(minePos, crystalPlacePos, supportPositions))
				continue;
			if (additionalSupport != null && isUnsafePosition(minePos, additionalSupport, supportPositions))
				priority -= 0.5f;

			if (type != AutoMineType.SURROUND_CEV && isReplaceable(crystalPlacePos) && !supportPositions.contains(
					crystalPlacePos))
				continue;

			float damage = type != AutoMineType.BURROW ? getDamage(
					target,
					minePos,
					crystalPlacePos,
					DamageCheck.EXPOSURE,
					config.getMinCevDamage().get(),
					supportPositions,
					type
			) : 100f;
			if (Helper.isCev(type) && damage < config.getMinCevDamage().get())
				continue;
			if (damage > _bestBlockDamage)
				_bestBlockDamage = damage;

			positions.add(new PositionInfo(minePos, supportPositions, crystalPlacePos, priority, damage, type));
		}

		return new CollectedPositions(positions, _bestBlockDamage);
	}

	public boolean cevRaycast(PlayerEntity target, BlockPos crystalPlacePos) {
		if (target == null)
			return false;

		Vec3d source = crystalPlacePos.toCenterPos().add(0, 0.5, 0);

		RaycastContext context = new RaycastContext(
				target.getPos(),
				source,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				target
		);
		RaycastContext context2 = new RaycastContext(
				target.getPos().add(0, 0.5, 0),
				source,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				target
		);

		BlockHitResult raycast = DamageCalculator.raycast(context, false, false, crystalPlacePos, null);
		BlockHitResult raycast2 = DamageCalculator.raycast(context2, false, false, crystalPlacePos, null);
		return (raycast != null && raycast.getType() == HitResult.Type.BLOCK) && (raycast2 != null
				&& raycast2.getType() == HitResult.Type.BLOCK
		);
	}

	public boolean isUnsafePosition(BlockPos mine, BlockPos crystal, List<BlockPos> support) {
		return getDamage(
				mc.player,
				mine,
				crystal,
				DamageCheck.FULL,
				config.getMaxSelfDamage().get(),
				support,
				AutoMineType.TRAP_CEV
		) > config.getMaxSelfDamage().get();
	}

	public float getDamage(
			PlayerEntity entity,
			BlockPos mine,
			BlockPos crystal,
			DamageCheck mode,
			double minDamage,
			List<BlockPos> support,
			AutoMineType type
	) {
		if (entity == null)
			return 0;
		Vec3d crystalVec3d = getCrystalVec3d(crystal);

		if (mode != DamageCheck.EXPOSURE && Helper.isCev(type) &&
				DamageCalculator.getExplosionDamage(
						entity,
						entity.getBoundingBox(),
						crystalVec3d,
						false,
						true,
						false,
						6,
						mine
				)
						< minDamage)
			return 0;

		if (mode == DamageCheck.PRE)
			return 1;
		return DamageCalculator.getExplosionDamage(
				entity,
				entity.getBoundingBox(),
				crystalVec3d,
				false,
				true,
				true,
				6,
				mine,
				support
		);
	}

	public enum DamageCheck {
		FULL,
		PRE,
		EXPOSURE
	}

	public record CollectedPositions(
			List<PositionInfo> result,
			double bestDamage
	) {

		public static CollectedPositions empty() {
			return new CollectedPositions(new ArrayList<>(), 0);
		}
	}
}