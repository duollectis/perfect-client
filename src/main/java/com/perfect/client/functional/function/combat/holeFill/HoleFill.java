package com.perfect.client.functional.function.combat.holeFill;

import com.google.common.collect.Lists;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.functions.hole.Hole;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.holeFill.helpers.HoleHelper;
import com.perfect.client.functional.function.combat.holeFill.helpers.TargetHelper;
import lombok.Getter;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;


public class HoleFill extends PlaceFunction {

	private final HoleFillConfig config = new HoleFillConfig(this);

	private final HoleHelper holeHelper = new HoleHelper(this, config);

	@Getter
	private final TargetHelper targetHelper = new TargetHelper(this, config);
	private List<BlockPos> poses = new ArrayList<>();

	public HoleFill() {
		super("hole-fill", "Hole Fill", Category.COMBAT, false, false, "Hole Filler");
	}

	@Override
	public String getStatus() {
		return String.valueOf(poses.size());
	}

	@Override
	protected List<BlockPos> getTrap() {
		poses = findHoles();
		return poses;
	}

	
	private List<BlockPos> findHoles() {
		List<BlockPos> holes = new ArrayList<>();

		if (mc.player == null || mc.world == null) {
			return null;
		}

		for (BlockPos pos : getSearchPositions()) {
			Hole hole = holeHelper.getHole(pos);

			if (holeHelper.isGoodHole(hole, pos)) {
				holes.addAll(Lists.newArrayList(hole.positions));
			}
		}

		return holes;
	}

	private List<BlockPos> getSearchPositions() {
		return PositionMath.getCube(config.getRange().get(), config.getRange().get());
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		return null;
	}

	@Override
	protected PlaceFunctionConfig<HoleFill> getConfig() {
		return config;
	}
}
