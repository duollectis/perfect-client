package com.perfect.client.functional.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.perfect.client.core.functional.command.Command;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.functional.command.arguments.ConfigArgumentType;
import com.perfect.client.manager.core.ConfigManager;
import net.minecraft.command.CommandSource;
import net.minecraft.util.Formatting;

import java.io.File;
import java.util.function.Consumer;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;


public class ConfigCommand extends Command {

	public ConfigCommand() {
		super("config", "cfg");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(command("save", true, file -> {
			ConfigManager.saveGeneral(file);
			sendMessage(file, "saved");
		}));

		builder.then(command("load", false, file -> {
			ConfigManager.loadGeneral(file);
			sendMessage(file, "loaded");
		}));

		builder.then(command("delete", false, file -> {
			if (file.delete()) {
				sendMessage(file, "deleted");
			}
		}));

		builder.then(literal("reset").executes(context -> {
			ConfigManager.resetGeneral();

			ChatUtils.sendMsg("%s%s".formatted(Formatting.WHITE, translation("reset")));

			return SINGLE_SUCCESS;
		}));
	}

	private LiteralArgumentBuilder<CommandSource> command(
			String name,
			boolean save,
			Consumer<File> consumer
	) {
		return literal(name).then(argument("config", new ConfigArgumentType(!save))
				.executes(context -> {
					File file = context.getArgument("config", File.class);
					consumer.accept(file);
					return SINGLE_SUCCESS;
				}));
	}

	private void sendMessage(File file, String translationId) {
		ChatUtils.sendMsg(
				file.hashCode(),
				"%s%s %s%s".formatted(
						Formatting.WHITE,
						file.getName(),
						Formatting.GRAY,
						translation(translationId)
				)
		);
	}
}