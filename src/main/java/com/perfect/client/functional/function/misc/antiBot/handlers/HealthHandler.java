package com.perfect.client.functional.function.misc.antiBot.handlers;

import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.player.PlayerEntity;

public class HealthHandler extends AntiBotHandler {

	public HealthHandler(
			AntiBot function,
			AntiBotConfig config
	) {
		super(function, config);
	}

	@Override
	public boolean isBot(PlayerEntity player) {
		return (int) EntityUtils.getTotalHealth(player) / player.getMaxHealth() == 0;
	}
}
