package com.perfect.client.functional.function.misc.antiBot.handlers;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovementHandler extends AntiBotHandler {

	private final Map<PlayerEntity, Boolean> moved = new ConcurrentHashMap<>();

	public MovementHandler(
			AntiBot function,
			AntiBotConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPostTick() {
		if (mc.world == null) {
			return;
		}

		for (PlayerEntity player : mc.world.getPlayers()) {
			if (EntityUtils.isMoving(player)) {
				moved.put(player, true);
			}
		}
	}

	@Override
	public boolean isBot(PlayerEntity player) {
		return !moved.getOrDefault(player, false);
	}

	@Override
	public void onDisable() {
		moved.clear();
	}

	@Override
	public void clear() {
		moved.clear();
	}
}
