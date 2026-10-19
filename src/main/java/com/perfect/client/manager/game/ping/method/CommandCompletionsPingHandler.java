package com.perfect.client.manager.game.ping.method;

import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.manager.game.ping.PingHandler;
import lombok.Setter;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;

import java.util.Random;

public class CommandCompletionsPingHandler extends PingHandler {

	private int id = -1;

	@Setter
	private String command;

	@Override
	public void sendPacket() {
		if (command != null && !command.isEmpty()) {
			PacketUtils.sendPacket(new RequestCommandCompletionsC2SPacket(
					new Random().nextInt(99999, 999999999),
					command.trim() + " "
			));
		}
	}

	@Override
	public void handleClientPacket(Packet<?> _packet) {
		if (isWaitingAnswer()) {
			return;
		}

		if (_packet instanceof RequestCommandCompletionsC2SPacket packet) {
			id = packet.getCompletionId();
			timer.reset();
		}
	}

	@Override
	public boolean handleServerPacket(Packet<?> _packet) {
		if (isWaitingAnswer() && _packet instanceof CommandSuggestionsS2CPacket packet && id == packet.id()) {
			id = -1;
			return true;
		}

		return false;
	}

	@Override
	public boolean isWaitingAnswer() {
		return id != -1;
	}

	@Override
	public void reset() {
		super.reset();
		id = -1;
	}
}
