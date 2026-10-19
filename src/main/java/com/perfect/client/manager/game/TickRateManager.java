package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.math.MathUtils;
import lombok.Getter;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;

public class TickRateManager extends Manager {

	private final float[] tickRates = new float[20];
	private int nextIndex = 0;
	private long timeLastTimeUpdate = -1;
	private long timeGameJoined;

	private long timeLastPacketReceived = -1;

	@Getter
	private float tps;

	public int getDelay(long delay) {
		return Math.round(delay / (tps / 20f));
	}

	@Override
	public void onGameJoin() {
		Arrays.fill(tickRates, 0);
		nextIndex = 0;
		timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		long now = System.currentTimeMillis();

		timeLastPacketReceived = now;

		if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
			float timeElapsed = (now - timeLastTimeUpdate) / 1000.0f;
			tickRates[nextIndex] = MathHelper.clamp(20.0f / timeElapsed, 0.0f, 20.0f);
			nextIndex = (nextIndex + 1) % tickRates.length;
			timeLastTimeUpdate = now;

			tps = (float) MathUtils.roundTo(calculateTickRate(), 2);
		}
	}

	private float calculateTickRate() {
		if (mc.world == null || mc.player == null) {
			return 0.0f;
		}

		if (System.currentTimeMillis() - timeGameJoined < 4000) {
			return 20.0f;
		}

		int numTicks = 0;
		float sumTickRates = 0.0f;

		for (float tickRate : tickRates) {
			if (tickRate > 0.0f) {
				sumTickRates += tickRate;
				numTicks++;
			}
		}

		return sumTickRates / numTicks;
	}

	public float getTimeSinceLastTick() {
		return System.currentTimeMillis() - timeLastTimeUpdate;
	}

	public float getTimeSinceLastPacketReceived() {
		return System.currentTimeMillis() - timeLastPacketReceived;
	}
}
