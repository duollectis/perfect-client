package com.perfect.client.manager.game.ping;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.manager.game.ping.method.CommandCompletionsPingHandler;
import com.perfect.client.manager.game.ping.method.StatsPingHandler;
import lombok.Getter;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.pingSpoof;

public class PingManager extends Manager {

	private final List<PingHandler> handlers = new ArrayList<>();

	@Getter
	private final PingHandler statsHandler = add(new StatsPingHandler());

	@Getter
	private final PingHandler commandCompletionsHandler = add(new CommandCompletionsPingHandler());

	@Getter
	private int ping;

	private boolean anyWaiting;

	public int getPingInTicks(boolean strict) {
		float tps = Managers.TICKRATE.getTps();

		double ticks = (tps / 1000.0) * ping;

		if (strict) {
			return (int) Math.ceil(ticks);
		}
		else {
			return (int) Math.round(ticks);
		}
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		anyWaiting = false;

		for (PingHandler handler : handlers) {
			handler.handleClientPacket(event.getPacket());

			if (!anyWaiting) {
				anyWaiting = handler.isWaitingAnswer();
			}
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof PlayerListS2CPacket packet) {
			loadPingFromPacket(packet);
			return;
		}

		if (anyWaiting) {
			for (PingHandler handler : handlers) {
				if (handler.handleServerPacket(event.getPacket())) {
					ping = handler.getPing();
				}
			}

			anyWaiting = false;

			for (PingHandler handler : handlers) {
				if (handler.isWaitingAnswer()) {
					anyWaiting = true;
					break;
				}
			}
		}
	}

	@Override
	public void onGameJoin() {
		reset();
	}

	@Override
	public void onGameLeft() {
		reset();
	}

	private void loadPingFromPacket(PlayerListS2CPacket packet) {
		if (mc.player == null || mc.player.getUuid() == null || pingSpoof.isEnabled()) {
			return;
		}

		for (PlayerListS2CPacket.Entry entry : packet.getEntries()) {
			if (mc.player.getUuid().equals(entry.profileId())) {
				ping = entry.latency();
				return;
			}
		}
	}

	private void reset() {
		ping = -1;
		handlers.forEach(PingHandler::reset);
	}

	private PingHandler add(PingHandler handler) {
		handlers.add(handler);
		return handler;
	}
}
