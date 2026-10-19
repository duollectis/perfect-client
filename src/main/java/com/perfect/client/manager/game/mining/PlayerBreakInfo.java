package com.perfect.client.manager.game.mining;

import com.perfect.client.core.manager.Managers;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import static com.perfect.client.PerfectClient.mc;

@Getter
@AllArgsConstructor
public class PlayerBreakInfo {

	private final int playerId;
	private final MiningSequence<BreakInfo> miningSequence;
	private final BreakInfo breakInfo;

	public BlockPos getPos() {
		return breakInfo.getPos();
	}

	public float getProgress(float speed) {
		return miningSequence.getProgress(breakInfo, speed);
	}

	public float getPrevProgress(float speed) {
		return miningSequence.getPrevProgress(breakInfo, speed);
	}

	public boolean isFriend() {
		if (mc.world == null) {
			return false;
		}
		Entity entity = mc.world.getEntityById(playerId);

		return Managers.FRIEND.isFriend(entity);
	}
}
