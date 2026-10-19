package com.perfect.client.functional.function.misc.antiBot.handlers;

import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class SpeedHandler extends AntiBotHandler {

	public SpeedHandler(
			AntiBot function,
			AntiBotConfig config
	) {
		super(function, config);
	}

	private final Map<PlayerEntity, Vec3d> prevPoses = new HashMap<>();
	private final Map<PlayerEntity, TimerUtils> lastMovementTimestamps = new HashMap<>();
	private final Map<PlayerEntity, Boolean> botMap = new HashMap<>();

	@Override
	public void onPostTick() {
		if (mc.world == null) {
			return;
		}

		for (PlayerEntity player : mc.world.getPlayers()) {
			Vec3d currentPos = new Vec3d(player.getX(), player.getY(), player.getZ());
			Vec3d lastPos = prevPoses.get(player);

			TimerUtils timer = lastMovementTimestamps.get(player);

			if (lastPos != null && timer != null) {
				if (timer.getMillis() > 0) {
					double distance = currentPos.distanceTo(lastPos);
					double speed = distance / timer.getSeconds();

					if (speed > 20.0) {
						botMap.put(player, true);
					}
				}
			}

			botMap.putIfAbsent(player, false);
			prevPoses.put(player, currentPos);
			lastMovementTimestamps.put(player, new TimerUtils());
		}
	}

	@Override
	public boolean isBot(PlayerEntity player) {
		return botMap.getOrDefault(player, false);
	}

	@Override
	public void onDisable() {
		clear();
	}

	@Override
	public void clear() {
		prevPoses.clear();
		lastMovementTimestamps.clear();
		botMap.clear();
	}
}