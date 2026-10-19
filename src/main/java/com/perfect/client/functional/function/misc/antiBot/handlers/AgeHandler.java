package com.perfect.client.functional.function.misc.antiBot.handlers;

import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.player.PlayerEntity;

public class AgeHandler extends AntiBotHandler {

	public AgeHandler(
			AntiBot function,
			AntiBotConfig config
	) {
		super(function, config);
	}

	@Override
	public boolean isBot(PlayerEntity player) {
		return player.age < 20;
	}
}
