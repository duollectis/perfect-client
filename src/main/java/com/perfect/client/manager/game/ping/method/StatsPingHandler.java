package com.perfect.client.manager.game.ping.method;

import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.manager.game.ping.PingHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;

public class StatsPingHandler extends PingHandler {

	private boolean waitingAnswer;

	@Override
	public void sendPacket() {
		PacketUtils.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
	}

	@Override
	public void handleClientPacket(Packet<?> _packet) {
		if (isWaitingAnswer()) {
			return;
		}

		if (_packet instanceof ClientStatusC2SPacket packet) {
			if (packet.getMode() == ClientStatusC2SPacket.Mode.REQUEST_STATS) {
				waitingAnswer = true;
				timer.reset();
			}
		}
	}

	@Override
	public boolean handleServerPacket(Packet<?> _packet) {
		if (isWaitingAnswer() && _packet instanceof StatisticsS2CPacket) {
			waitingAnswer = false;
			return true;
		}

		return false;
	}

	@Override
	public void reset() {
		super.reset();
		waitingAnswer = false;
	}

	@Override
	public boolean isWaitingAnswer() {
		return waitingAnswer;
	}
}
