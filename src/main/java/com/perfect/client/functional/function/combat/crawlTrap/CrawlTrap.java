package com.perfect.client.functional.function.combat.crawlTrap;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.functions.target.TargetPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;


public class CrawlTrap extends PlaceFunction {

	private final CrawlTrapConfig config = new CrawlTrapConfig(this);

	public CrawlTrap() {
		super("crawl-trap", "Crawl Trap", Category.COMBAT, true, false);
	}

	@Override
	protected List<BlockPos> getTrap() {
		LivingEntity target = getTarget();

		if (target == null) {
			return null;
		}

		return getTrap(target);
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			if (!BlockUtils.isReplaceable(pos)) {
				continue;
			}

			EndCrystalEntity crystal = getCrystalByBlock(pos);

			if (crystal != null) {
				return crystal;
			}
		}

		return null;
	}

	private LivingEntity getTarget() {
		return TargetUtils.getTarget(config.getRange().get(), TargetPriority.FIELD_OF_VIEW, false, this::isGoodTarget);
	}

	private List<BlockPos> getTrap(LivingEntity entity) {
		List<BlockPos> sphere = new ArrayList<>();

		for (BlockPos pos : getTrapBlocks(entity)) {
			double distance = PositionMath.squareDistanceHorizontal(entity.getPos(), pos.toCenterPos());

			if (distance <= config.getTrapRadius().get()) {
				sphere.add(pos);
			}
		}

		return sphere;
	}

	private List<BlockPos> getTrapBlocks(LivingEntity entity) {
		BlockPos head = getHeadPos(entity);
		BlockPos feet = getFeetPos(entity);

		List<BlockPos> trap = new ArrayList<>();

		trap.addAll(getTrapFromCenter(head));
		trap.addAll(getTrapFromCenter(feet));

		return trap;
	}

	private List<BlockPos> getTrapFromCenter(BlockPos center) {
		return PositionMath.getCube(
				center,
				config.getRange().getInt() + 1,
				0,
				this::isGoodPlacePos
		);
	}

	private boolean isTrapped(LivingEntity entity) {
		for (BlockPos pos : getTrap(entity)) {
			if (BlockUtils.isReplaceable(pos)) {
				return false;
			}
		}
		return true;
	}

	private boolean isGoodPlacePos(BlockPos pos) {
		if (!BlockUtils.isReplaceable(pos)) {
			return false;
		}
		if (BlockUtils.isOutOfBorder(pos)) {
			return false;
		}
		if (PositionMath.isOutOfRange(pos.toCenterPos(), config.getRange().get())) {
			return false;
		}

		if (config.getBreakCrystals().get()) {
			return !BlockUtils.hasEntity(
					pos,
					entity -> entity.intersectionChecked && !(entity instanceof EndCrystalEntity)
			);
		}
		else {
			return !BlockUtils.hasEntity(pos);
		}
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return false;
		}
		if (entity.getPose() != EntityPose.SWIMMING) {
			return false;
		}
		return !isTrapped(entity);
	}

	private BlockPos getHeadPos(LivingEntity entity) {
		return BlockPos.ofFloored(entity.getEyePos()).up();
	}

	private BlockPos getFeetPos(LivingEntity entity) {
		return entity.getBlockPos().down();
	}

	private EndCrystalEntity getCrystalByBlock(BlockPos pos) {
		List<Entity> entities = BlockUtils.getEntities(pos, entity -> entity instanceof EndCrystalEntity);

		if (!entities.isEmpty() && entities.getFirst() instanceof EndCrystalEntity crystal) {
			return crystal;
		}

		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		return null;
	}

	@Override
	protected PlaceFunctionConfig<CrawlTrap> getConfig() {
		return config;
	}
}
