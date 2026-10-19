package com.perfect.client.functional.function.misc.antiBot.utils;

import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import net.minecraft.entity.player.PlayerEntity;

public abstract class AntiBotHandler extends FunctionHandler<AntiBot, AntiBotConfig> {

	public AntiBotHandler(AntiBot function, AntiBotConfig config) {
		super(function, config);
	}

	public void clear() {}

	public abstract boolean isBot(PlayerEntity player);
}
