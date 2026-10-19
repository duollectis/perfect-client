package com.perfect.client.functional.function.player.packetMine.helper;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class PacketHelper extends FunctionHelper<PacketMine, PacketMineConfig> {

	public PacketHelper(PacketMine function, PacketMineConfig config) {
		super(function, config);
	}

	public void sendBlockStartPackets(BlockPos pos, Direction facing) {
		if (function.getLastClicked() != null) {
			sendAbortPacket(function.getLastClicked());
		}

		if (config.getGrim().get()) {
			sendPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing);
			//sendPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing);

			//sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
		}
		else {
			sendPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, facing);
		}
	}

	public void sendBlockBreakPackets(BlockPos pos, Direction facing) {
		if (config.getGrim().get()) {
			sendStopPacket(pos, facing);
		}
		else {
			sendStopPacket(pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, Direction.UP);
			sendPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, facing);
			sendPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, facing);
		}
	}

	private void sendAbortPacket(BlockPos pos) {
		sendPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, pos, getAbortDirection(pos));
	}

	private Direction getAbortDirection(BlockPos pos) {
		BlockHitResult hitResult = HitResultUtils.getInteractResult(pos);

		if (hitResult == null) {
			return Direction.DOWN;
		}

		return hitResult.getSide();
	}

	public void sendPacket(PlayerActionC2SPacket.Action action, BlockPos pos, Direction direction) {
		sendSequencedPacket(sequence -> new PlayerActionC2SPacket(action, pos, direction, sequence));
	}

	public void sendStopPacket(BlockPos pos, Direction direction) {
		if (mc.interactionManager == null) {
			return;
		}

		sendSequencedPacket(sequence -> {

			// Реализация клиент брейка из самой игры.
			// Правильно работает только при вызове из SequencedPacketCreator
			// вместе с STOP_DESTROY_BLOCK пакетом.

			if (config.getClientBreak().get()) {
				mc.interactionManager.breakBlock(pos);
			}
			return new PlayerActionC2SPacket(
					PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK,
					pos,
					direction,
					sequence
			);
		});
	}
}
