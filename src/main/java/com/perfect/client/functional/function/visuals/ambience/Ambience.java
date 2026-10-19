package com.perfect.client.functional.function.visuals.ambience;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.game.world.WorldUtils;
import lombok.Getter;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

import java.awt.*;


public class Ambience extends Function {

	private static final int FOG_FACTOR = 30;

	private final AmbienceConfig config = new AmbienceConfig(this);

	private long oldTime;

	public Ambience() {
		super("ambience", "Ambience", Category.VISUALS, "Visuals");
	}

	public void onWorldTimeChanged(boolean enabled) {
		if (mc.world == null || !isEnabled()) {
			return;
		}

		if (enabled) {
			oldTime = mc.world.getTime();
		}
		else {
			mc.world.setTime(oldTime, oldTime, true);
		}
	}

	@Override
	public void onPostTick() {
		if (mc.world != null && config.getWorldTime().get()) {
			long time = getTime();
			mc.world.setTime(time, time, true);
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (event.getPacket() instanceof WorldTimeUpdateS2CPacket packet && config.getWorldTime().get()) {
			oldTime = packet.timeOfDay();
			event.cancel();
		}
	}

	public long getTime() {
		if (config.getTimeMode().get() == Time.CUSTOM) {
			return config.getCustomTime().get();
		}
		return config.getTimeMode().get().getTime();
	}

	public Color skyColor() {
		return switch (WorldUtils.getDimension()) {
			case OVERWORLD -> config.getOverworldSkyColor().get().getColor();
			case NETHER -> config.getNetherSkyColor().get().getColor();
			case END -> config.getEndSkyColor().get().getColor();
		};
	}

	public boolean getCustomSkyColor() {
		return config.getCustomSkyColor().get();
	}

	public Color fogColor() {
		return switch (WorldUtils.getDimension()) {
			case OVERWORLD -> config.getOverworldFogColor().get().getColor();
			case NETHER -> config.getNetherFogColor().get().getColor();
			case END -> config.getEndFogColor().get().getColor();
		};
	}

	public boolean getCustomFog() {
		return config.getCustomFog().get();
	}

	public boolean getThickFog() {
		return config.getThickFog().get();
	}

	public boolean getCustomFogColor() {
		return config.getCustomFogColor().get();
	}

	public boolean getCustomFogRange() {
		return config.getCustomFogRange().get();
	}

	public float getFogStart() {
		return config.getFogRange().getFrom().floatValue() * FOG_FACTOR;
	}

	public float getFogEnd() {
		return config.getFogRange().getTo().floatValue() * FOG_FACTOR;
	}

	public boolean getFlatSky() {
		return config.getFlatSky().get();
	}

	public boolean getParticles() {
		return config.getParticles().get();
	}

	public MultiColor getParticleColor() {
		return config.getParticleColor().get();
	}

	@Getter
	public enum Time {
		EVENING(12800),
		NIGHT(-1300),
		DAY(-20000),
		CUSTOM(0);

		private final int time;

		Time(int time) {
			this.time = time;
		}
	}
}
