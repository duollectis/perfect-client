package com.perfect.client.manager.game.ping;

import com.perfect.client.core.utils.math.TimerUtils;
import net.minecraft.network.packet.Packet;

public abstract class PingHandler {

	protected final TimerUtils timer = new TimerUtils();

	public abstract void sendPacket();

	public abstract void handleClientPacket(Packet<?> _packet);
	public abstract boolean handleServerPacket(Packet<?> _packet);
	public abstract boolean isWaitingAnswer();

	public void reset() {
		timer.reset();
	}

	public int getPing() {
		return (int) timer.getMillis();
	}
}
