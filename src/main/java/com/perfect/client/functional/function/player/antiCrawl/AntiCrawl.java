package com.perfect.client.functional.function.player.antiCrawl;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.core.utils.Utils.add;
import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class AntiCrawl extends Function {

	private final AntiCrawlConfig config = new AntiCrawlConfig(this);

	public AntiCrawl() {
		super("anti-crawl", "Anti Crawl", Category.PLAYER);
	}

	@Override
	public void onRotate() {
		if (mc.world == null || mc.player == null) {
			return;
		}

		if (mc.player.getPose() != EntityPose.SWIMMING) {
			resetMining();
			return;
		}

		for (BlockPos pos : getTargetBlocks()) {
			if (!isGoodBlock(pos)) {
				continue;
			}

			if (isQueueFull(MiningPriority.ANTI_CRAWL)) {
				break;
			}

			packetMine.startBreaking(pos, Direction.DOWN, MiningPriority.ANTI_CRAWL, getId());
		}
	}

	private void resetMining() {
		packetMine.removeIf(info -> info.getFunctionId().equals(getId()));
	}

	public boolean isQueueFull(int priority) {
		if (!packetMine.isFull()) {
			return false;
		}
		for (ClientBreakInfo info : packetMine.getAll()) {
			if (info.getPriority() < priority) {
				return false;
			}
		}
		return true;
	}

	private boolean isGoodBlock(BlockPos pos) {
		return !BlockUtils.isReplaceable(pos)
				&& BlockUtils.isBreakable(pos)
				&& BlockUtils.isCollidable(pos);
	}

	private List<BlockPos> getTargetBlocks() {
		ArrayList<BlockPos> list = new ArrayList<>();

		if (mc.world == null || mc.player == null) {
			return list;
		}

		if (config.getSingleBlock().get()) {
			list.add(BlockPos.ofFloored(mc.player.getEyePos()).up());
			return list;
		}
		else {
			return getHeadTrap(list);
		}
	}

	private List<BlockPos> getHeadTrap(ArrayList<BlockPos> list) {
		if (mc.player == null) {
			return list;
		}

		Box b = mc.player.getBoundingBox().expand(-EntityUtils.ANTI_FLOOR_OFFSET);

		add(list, BlockPos.ofFloored(b.minX, b.maxY + 1, b.minZ));
		add(list, BlockPos.ofFloored(b.minX, b.maxY + 1, b.maxZ));
		add(list, BlockPos.ofFloored(b.maxX, b.maxY + 1, b.minZ));
		add(list, BlockPos.ofFloored(b.maxX, b.maxY + 1, b.maxZ));

		return list;
	}
}