package com.perfect.client.functional.function.player.packetMine.handlers;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.functional.function.player.packetMine.PacketMine;
import com.perfect.client.functional.function.player.packetMine.PacketMineConfig;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import com.perfect.client.functional.function.player.packetMine.data.RebreakInfo;
import com.perfect.client.functional.function.player.packetMine.utils.PacketMineHandler;
import com.perfect.client.manager.game.mining.MiningSequence;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;

@Getter

public class RebreakHandler extends PacketMineHandler {

	private RebreakInfo rebreakInfo;

	public RebreakHandler(
			PacketMine function,
			PacketMineConfig config,
			MiningSequence<ClientBreakInfo> miningSequence
	) {
		super(function, config, miningSequence);
	}

	@Override
	public void onDisable() {
		reset();
	}

	@Override
	public void onGameLeft() {
		reset();
	}

	@Override
	public void onPostPacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof PlayerRespawnS2CPacket) {
			reset();
		}
	}

	public void reset() {
		rebreakInfo = null;
	}

	public void setRebreakInfo(ClientBreakInfo info, boolean airPreBreak) {
		setRebreakInfo(info, info.getBlockState(), airPreBreak);
	}

	public void setRebreakInfo(ClientBreakInfo info, BlockState state, boolean airPreBreak) {
		rebreakInfo = new RebreakInfo(info, airPreBreak);
		rebreakInfo.setBlockState(state);
	}

	public void tick() {
		if (rebreakInfo != null) {
			rebreakInfo.tick();
		}
	}

	public void setTicks(int ticks) {
		rebreakInfo.setTicks(ticks);
	}

	public int getTicks() {
		return rebreakInfo.getTicks();
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (mc.player == null) {
			return;
		}
		if (rebreakInfo == null) {
			return;
		}
		if (!event.getPos().equals(rebreakInfo.getPos())) {
			return;
		}
		if (!event.isPlace()) {
			return;
		}

		if (!miningSequence.put(rebreakInfo)) {
			return;
		}

		BlockState state = event.getNewState();

		if (rebreakInfo.isAirPreBreak()) {
			rebreakFast(state);
		}
		else {
			switch (config.getRebreak().get()) {
				case NORMAL -> restartRebreak();
				case FAST -> rebreakFast(state);
				case INSTANT -> rebreakInstant();
			}
		}

		rebreakInfo = null;
	}

	public void restartRebreak() {
		if (rebreakInfo != null) {
			function.startBreaking(rebreakInfo);
		}
	}

	private void rebreakFast(BlockState state) {
		float blockStrength = function.getBlockStrength(rebreakInfo.getPos(), state);

		float progress = getFastRebreakProgress(blockStrength);

		rebreakInfo.addProgress(progress);
		rebreakInfo.updatePrevProgress();
	}

	private void rebreakInstant() {
		rebreakInfo.forceFinish();
	}

	public float getFastRebreakProgress(float blockStrength) {
		return getFastRebreakProgress(blockStrength, 0);
	}

	public float getFastRebreakProgress(float blockStrength, int extrapolation) {
		int ticks = rebreakInfo.getTicks() + extrapolation;
		return blockStrength * ticks * config.getSpeed().getFloat();
	}
}
