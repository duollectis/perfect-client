package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.collection.UpdatingMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PendingManager extends Manager {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	private final UpdatingMap<BlockPos, BlockState> positions = new UpdatingMap<>();

	private boolean shouldRun = false;

	@Override
	public void onGameJoin() {
		run();
	}

	@Override
	public void onGameLeft() {
		stop();
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (event.isPlace())
			positions.remove(event.getPos());
	}

	private void run() {
		positions.clear();
		shouldRun = true;

		executor.submit(() -> {
			while (shouldRun) {
				Utils.sleep(100);
				update();
			}
		});
	}

	private void stop() {
		shouldRun = false;
	}

	private void update() {
		positions.update(Managers.PING.getPing() + 10);
	}

	public boolean contains(BlockPos pos) {
		return positions.containsKey(pos);
	}

	public void add(BlockPos pos, BlockState state) {
		positions.add(pos, state);
	}

	public void add(BlockHitResult result, BlockState state) {
		if (mc.world == null)
			return;
		BlockPos
				pos =
				mc.world.getBlockState(result.getBlockPos()).isReplaceable() && !contains(result.getBlockPos())
				? result.getBlockPos() : result.getBlockPos().offset(result.getSide());
		positions.add(pos, state);
	}

	public BlockState getState(BlockPos pos) {
		if (!positions.containsKey(pos))
			return Blocks.AIR.getDefaultState();
		return positions.getValue(pos);
	}
}
