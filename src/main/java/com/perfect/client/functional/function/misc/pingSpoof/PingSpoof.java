package com.perfect.client.functional.function.misc.pingSpoof;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;

import static com.perfect.client.manager.system.FunctionManager.fastLatency;

public class PingSpoof extends Function {

	private final PingSpoofConfig config = new PingSpoofConfig(this);

	private volatile Packet<?> lastPacket;

	public PingSpoof() {
		super("ping-spoof", "Ping Spoof", Category.MISC);
	}

	@Override
	protected void onEnable() {
		fastLatency.enable();
	}

	@Override
	public void onDisable() {
		if (lastPacket != null) {
			sendPacket(lastPacket);
		}
	}

	@Override
	public void onPreTick() {
		fastLatency.enable();
	}

	@Override
	public void onGameLeft() {
		lastPacket = null;
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof KeepAliveS2CPacket packet) {
			event.cancel();

			lastPacket = new KeepAliveC2SPacket(packet.getId());

			long delay = config.getDelay().get();

			if (config.getCorrection().get()) {
				delay = Math.max(delay - Managers.PING.getPing(), 0);
			}

			Managers.ASYNC.run(delay, () -> {
				if (lastPacket != null && isEnabled()) {
					sendPacket(lastPacket);
				}

				lastPacket = null;
			});
		}
	}
}
