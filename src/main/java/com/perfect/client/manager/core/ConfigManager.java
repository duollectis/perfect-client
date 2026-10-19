package com.perfect.client.manager.core;

import com.google.common.collect.Lists;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.configs.ConfigIdentifier;
import com.perfect.client.core.utils.files.FileUtils;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.JsonUtils;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

	public static final File SAVED_CONFIGS = new File(PerfectClient.FOLDER, "saved-configs");
	public static final File IMAGE_CHAMS = new File(PerfectClient.FOLDER, "chams.png");
	public static final File AUTO_EZ_MESSAGES = new File(PerfectClient.FOLDER, "auto-ez.txt");

	private static final String CONFIG_EXTENSION = ".json";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private static final File GENERAL_FILE = toConfigFile(PerfectClient.FOLDER, "config");
	private static final File FRIENDS_FILE = toConfigFile(PerfectClient.FOLDER, "friends");

	private static boolean loaded = false;

	public static File toConfigFile(File folder, String name) {
		return new File(folder, "%s%s".formatted(name, CONFIG_EXTENSION));
	}

	
	public static void init() {
		FileUtils.mkdirs(SAVED_CONFIGS);

		FileUtils.create(AUTO_EZ_MESSAGES);

		new ConfigManagerEventListener();
	}

	public static void resetGeneral() {
		Managers.getGeneralSystems().forEach(ISystem::reset);
	}

	
	public static void loadAll() {
		loadGeneral(GENERAL_FILE);
		execute(Action.LOAD, FRIENDS_FILE, Managers.FRIEND);
	}

	
	public static void loadGeneral(File file) {
		execute(Action.LOAD, file, Managers.getGeneralSystems());
	}

	
	public static void loadIternal(File file, List<ISystem<?>> systems) {
		loaded = false;

		if (file.exists()) {
			JsonObject json = JsonUtils.getJson(file);

			if (json == null) {
				return;
			}

			for (ISystem<?> system : systems) {
				try {
					system.reset();

					JsonObject systemJson = json.getJsonObject(system.getId());

					if (systemJson == null) {
						continue;
					}

					system.fromJson(systemJson);
				}
				catch (Exception e) {
					LoggerUtils.error("Failed to load config:", e);
				}
			}
		}

		loaded = true;
	}

	
	public static void saveAll() {
		saveGeneral(GENERAL_FILE);
		execute(Action.SAVE, FRIENDS_FILE, Managers.FRIEND);
	}

	
	public static void saveGeneral(File file) {
		execute(Action.SAVE, file, Managers.getGeneralSystems());
	}

	
	private static void saveIternal(File file, List<ISystem<?>> systems) {
		if (!loaded) {
			return;
		}

		JsonObject json = new JsonObject();

		json.add("author", "username");
		json.add("date", LocalDate.now().format(DATE_FORMAT));

		for (ISystem<?> system : systems) {
			JsonObject systemJson = system.toJson();

			if (systemJson == null) {
				continue;
			}

			json.add(system.getId(), systemJson);
		}

		JsonUtils.writeJsonToFile(json, file);
	}

	
	public static List<String> getConfigFileNames() {
		File[] files = ConfigManager.SAVED_CONFIGS.listFiles();
		List<String> names = new ArrayList<>();

		if (files == null) {
			return names;
		}

		for (File file : files) {
			if (file.isFile()) {
				names.add(FilenameUtils.removeExtension(file.getName()));
			}
		}

		return names;
	}

	
	private static void execute(Action action, File file, ISystem<?> system) {
		execute(action, file, Lists.newArrayList(system));
	}

	
	private static void execute(Action action, File file, List<ISystem<?>> systems) {
		switch (action) {
			case SAVE -> saveIternal(file, systems);
			case LOAD -> loadIternal(file, systems);
		}
	}

	public static ConfigIdentifier parseAuthorAndDate(File file) {
		if (file == null || !file.exists()) {
			return ConfigIdentifier.UNKNOWN;
		}

		try {
			JsonObject json = JsonUtils.getJson(file);

			String author = json.getString("author");
			String date = json.getString("date");

			return new ConfigIdentifier(author, date);
		}
		catch (Exception exception) {
			return ConfigIdentifier.UNKNOWN;
		}
	}

	private enum Action {
		SAVE,
		LOAD
	}

	public static class ConfigManagerEventListener extends EventListener {

		@Override
		public void onGameLeft() {
			saveAll();
		}

		@Override
		public void onShutdown() {
			saveAll();
		}
	}
}
