package com.perfect.client.functional.function.player.packetMine.handlers;

import com.perfect.client.core.event.events.block.BlockEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.functions.mining.MiningUtils;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.functional.function.player.packetMine.utils.PacketMineHandler;
import com.perfect.client.manager.game.mining.MiningSequence;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class AttackBlockHandler extends PacketMineHandler {

	public AttackBlockHandler(
			PacketMine function,
			PacketMineConfig config,
			MiningSequence<ClientBreakInfo> miningSequence
	) {
		super(function, config, miningSequence);
	}

	@Override
	public void onAttackBlock(BlockEvent event) {
		onAttackBlock(event, event.getPos(), event.getDirection());
	}

	@Override
	public void onBlockBreakingProgress(BlockEvent event) {
		onAttackBlock(event, event.getPos(), event.getDirection());
	}

	private void onAttackBlock(Cancellable event, BlockPos pos, Direction direction) {
		if (onAttackBlock(pos, direction)) {
			event.cancel();
		}
	}

	
	private boolean onAttackBlock(BlockPos pos, Direction direction) {
		if (!canStartMining(pos)) {
			miningSequence.clear();
			function.resetRebreak();
			return false;
		}

		function.startBreaking(pos, direction, MiningPriority.PACKET_MINE, function.getId());
		return true;
	}

	private boolean canStartMining(BlockPos pos) {
		if (mc.player == null || mc.player.getAbilities().creativeMode) {
			return false;
		}
		return MiningUtils.canBreak(pos);
	}
}
