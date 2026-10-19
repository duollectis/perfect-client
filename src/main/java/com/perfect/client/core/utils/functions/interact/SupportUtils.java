
package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;

public class SupportUtils {

	public static void addSupport(List<BlockPos> list, boolean support, int length) {
		addSupport(list, support, length, null);
	}

	public static void addSupport(List<BlockPos> list, boolean support, int length, List<BlockPos> exempt) {
		if (clientConfig.getAirPlace() || list.isEmpty()) {
			return;
		}

		List<BlockPos> removed = new ArrayList<>();
		List<BlockPos> added = new ArrayList<>();
		List<BlockPos> accepted = new ArrayList<>();

		int supportLength = support ? length : 0;

		for (BlockPos pos : list) {
			List<BlockPos> supportBlocks = new ArrayList<>();

			supportBlocks.addAll(added);
			supportBlocks.addAll(accepted);

			boolean found = findNeighbour(supportBlocks, pos, supportLength, 0, exempt);

			if (!found) {
				removed.add(pos);
			}
			else {
				added.addAll(supportBlocks);
				accepted.add(pos);
			}
		}

		if (support) {
			for (BlockPos addPos : added) {
				Utils.addFirst(list, addPos);
			}
		}

		list.removeAll(removed);
	}

	
	public static boolean findNeighbour(
			List<BlockPos> list,
			BlockPos pos,
			int supportAmount,
			int iteration,
			List<BlockPos> exempt
	) {
		if (iteration > supportAmount || mc.world == null) {
			return false;
		}

		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = pos.offset(direction);
			if (!hasEntity(offsetPos)
					&& !isExempted(pos, exempt)
					&& (list.contains(offsetPos) || !isReplaceable(offsetPos) || Managers.PENDING.contains(offsetPos))
					&& (!isReplaceable(pos) || HitResultUtils.getPlaceResult(pos, list) != null)) {
				return true;
			}
		}

		for (Direction direction : Direction.values()) {
			BlockPos offsetPos = pos.offset(direction);
			if (!hasEntity(offsetPos)
					&& !isExempted(pos, exempt)
					&& findNeighbour(list, offsetPos, supportAmount, iteration + 1, exempt)
					&& HitResultUtils.getPlaceResult(offsetPos, list) != null) {

				Utils.addFirst(list, offsetPos);
				return true;
			}
		}

		return false;
	}

	
	private static boolean isExempted(BlockPos pos, List<BlockPos> exempt) {
		if (exempt == null) {
			return false;
		}
		return exempt.contains(pos);
	}

	
	private static boolean hasEntity(BlockPos pos) {
		return BlockUtils.hasEntity(pos, SupportUtils::isObstructiveEntity);
	}

	
	private static boolean isObstructiveEntity(Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return false;
		}
		return entity.intersectionChecked;
	}
}
