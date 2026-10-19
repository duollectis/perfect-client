package com.perfect.client.functional.function.combat.autoMine.data;

import com.perfect.client.functional.function.combat.autoMine.helpers.AutoMineType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@Setter
@Getter
public class PositionInfo {

	private BlockPos minePosition;
	private List<BlockPos> supportPositions;
	private BlockPos crystalPosition;
	private float damage;
	private AutoMineType type = AutoMineType.NONE;
	private float priority;

	public PositionInfo() {
	}

	public PositionInfo(
			BlockPos minePosition,
			List<BlockPos> supportPositions,
			BlockPos crystalPosition,
			float priority,
			float damage,
			AutoMineType type
	) {
		this.minePosition = minePosition;
		this.supportPositions = supportPositions;
		this.crystalPosition = crystalPosition;
		this.damage = damage;
		this.priority = priority;
		this.type = type;
	}

	public boolean found() {
		return type != AutoMineType.NONE;
	}

	@Override
	public String toString() {
		return type.toString() + "; minePosition: " + minePosition + "; crystalPosition: " + crystalPosition;
	}
}