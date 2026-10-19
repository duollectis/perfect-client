package com.perfect.client.functional.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;


public class HudCommand extends Command {

	public HudCommand() {
		super("hud", "hud");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(literal("reset").executes(context -> {
			Managers.HUD.reset();

			ChatUtils.sendMsg("%s%s".formatted(Formatting.WHITE, translation("reset")));

			return SINGLE_SUCCESS;
		}));
	}
}