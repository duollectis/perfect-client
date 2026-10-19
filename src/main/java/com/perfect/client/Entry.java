package com.perfect.client;

import com.perfect.client.core.lang.Translations;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PreviousScreen;
import com.perfect.client.core.utils.DevUtils;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.files.FileUtils;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.gui.widgets.ClickGuiScreen;
import com.perfect.client.manager.core.ConfigManager;
import lombok.Getter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.MinecraftClient;

import static com.perfect.client.PerfectClient.*;

public class Entry implements ModInitializer {

	@Getter
	private static final String CLIENT_VERSION = "1.2";

	@Getter
	private static final String MINECRAFT_VERSION = "1.21.8";
	public static final String DISPLAY_VERSION = "Beta#3";

	@Override
	public void onInitialize() {
		long ms = System.currentTimeMillis();
		LoggerUtils.message("Initialize...");

		ModMetadata metadata = FabricLoader
				.getInstance()
				.getModContainer(MOD_ID).get()
				.getMetadata();

		if (!NAME.equals(metadata.getName())
				|| !MOD_ID.equals(metadata.getId())
				|| !DISPLAY_VERSION.equals(metadata.getVersion().getFriendlyString())
				|| !DISCORD.equals(metadata.getContact().get("discord").get())
				|| metadata.getAuthors().size() != AUTHORS.size()
		) {
			throw new RuntimeException("Init failed");
		}

		for (Person person : metadata.getAuthors()) {
			if (AUTHORS.contains(person.getName())) {
				continue;
			}

			throw new RuntimeException("Init failed");
		}

		mc = MinecraftClient.getInstance();

		if (!FOLDER.exists()) {
			FileUtils.mkdirs(FOLDER.getParentFile());
			FileUtils.mkdirs(FOLDER);
		}

		Translations.init();

		Managers.init();
		ConfigManager.init();
		ConfigManager.loadAll();

		DevUtils.init();
		PreviousScreen.init();
		ChatUtils.init();

		clickGuiScreen = new ClickGuiScreen();

		PerfectClient.onInit();

		LoggerUtils.message("Initialize finished in %ss".formatted(decimalFormat.format((System.currentTimeMillis() - ms) / 1000.0)));

		Runtime.getRuntime().addShutdownHook(new Thread(EVENT_MANAGER::onShutdown));

		PerfectClient.setInitialized(true);
	}
}
