package com.perfect.client.functional.function.player.packetMine.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.manager.game.mining.MiningSequence;

public class PacketMineHandler extends FunctionHandler<PacketMine, PacketMineConfig> {

	protected final MiningSequence<ClientBreakInfo> miningSequence;

	public PacketMineHandler(
			PacketMine function,
			PacketMineConfig config,
			MiningSequence<ClientBreakInfo> miningSequence
	) {
		super(function, config);
		this.miningSequence = miningSequence;
	}
}
