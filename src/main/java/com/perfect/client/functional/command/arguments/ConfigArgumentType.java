package com.perfect.client.functional.command.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.perfect.client.core.functional.command.Argument;
import com.perfect.client.manager.core.ConfigManager;
import net.minecraft.text.Text;

import java.io.File;
import java.util.stream.Stream;

public class ConfigArgumentType extends Argument<File> {

	private static final DynamicCommandExceptionType UNKNOWN_FILE = new DynamicCommandExceptionType(
			name -> Text.literal("Unknown file")
	);

	private final boolean onlyExisting;

	public ConfigArgumentType(boolean onlyExisting) {
		this.onlyExisting = onlyExisting;
	}

	
	@Override
	public File parse(String argument) throws CommandSyntaxException {
		File file = ConfigManager.toConfigFile(ConfigManager.SAVED_CONFIGS, argument);

		if (onlyExisting && !file.exists()) {
			throw UNKNOWN_FILE.create(argument);
		}

		return file;
	}

	@Override
	protected Stream<String> getCandidates() {
		return ConfigManager.getConfigFileNames().stream();
	}
}