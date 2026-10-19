package com.perfect.client.manager.game.mining;

import com.perfect.client.core.utils.game.world.BlockUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

@Getter
public class BreakInfo {

	private final BlockPos pos;
	private final int priority;

	@Setter
	private BlockState blockState;

	private float progress = 0;
	private float prevProgress = 0;

	private boolean doubleMined = false;
	private boolean finishForced = false;

	public BreakInfo(BlockPos pos, int priority) {
		this.pos = pos;
		this.priority = priority;

		updateState();
	}

	public void updateState() {
		blockState = BlockUtils.getState(pos);
	}

	public void addProgress(float blockStrength) {
		updatePrevProgress();
		progress += blockStrength;
	}

	public void updatePrevProgress() {
		prevProgress = progress;
	}

	public void setDoubleMined() {
		doubleMined = true;
	}

	public void forceFinish() {
		finishForced = true;
	}

	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}
