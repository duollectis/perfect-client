package com.perfect.client.functional.function.misc.antiBot.handlers;

import com.perfect.client.functional.function.misc.antiBot.AntiBot;
import com.perfect.client.functional.function.misc.antiBot.AntiBotConfig;
import com.perfect.client.functional.function.misc.antiBot.utils.AntiBotHandler;
import net.minecraft.entity.player.PlayerEntity;

public class NicknameHandler extends AntiBotHandler {

	public NicknameHandler(
			AntiBot function,
			AntiBotConfig config
	) {
		super(function, config);
	}

	@Override
	public boolean isBot(PlayerEntity player) {
		String name = player.getName().getString();
		return isBotName(name.toLowerCase());
	}

	private boolean isBotName(String name) {
		return name.contains("[npc]") || name.isBlank();
	}
}
