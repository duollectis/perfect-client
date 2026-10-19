package com.perfect.client.core.utils.files.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.files.FileUtils;
import com.perfect.client.core.utils.files.StreamUtils;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;

import java.io.*;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JsonUtils {

	private static final Gson formatter = new GsonBuilder().setPrettyPrinting().create();

	public static Map<String, String> parseAsMap(String path) {
		return JsonMapParser.parse(path);
	}

	public static JsonObject getResourceJson(String path) {
		return getJson(() -> PerfectClient.getResource(path));
	}

	public static JsonObject getJson(File file) {
		return getJson(() -> {
			try {
				return new FileInputStream(file);
			}
			catch (FileNotFoundException e) {
				return null;
			}
		});
	}

	public static JsonObject getJson(Supplier<InputStream> inputStreamSupplier) {
		try (InputStream inputStream = inputStreamSupplier.get()) {
			if (inputStream == null) {
				return null;
			}

			String string = new String(inputStream.readAllBytes());
			JsonElement<?> element = parseString(string);

			if (element.isJsonObject()) {
				return element.asJsonObject();
			}
		}
		catch (IOException e) {
			LoggerUtils.warning(e);
		}

		return null;
	}

	public static JsonElement<?> parseString(String string) {
		return new JsonElement<>(JsonParser.parseString(string));
	}

	public static void writeJsonToFile(JsonObject json, File file) {
		File tempFile;

		try {
			tempFile = File.createTempFile(PerfectClient.MOD_ID, file.getName());
		}
		catch (Exception exception) {
			LoggerUtils.warning(exception);
			return;
		}

		try (FileWriter writer = new FileWriter(tempFile)) {
			formatter.toJson(json.getJsonElement(), writer);
		}
		catch (IOException e) {
			LoggerUtils.warning(e);
			return;
		}

		FileUtils.mkdirs(file.getParentFile());

		StreamUtils.copy(tempFile, file);
		FileUtils.delete(tempFile);
	}

	public static void removeNulls(JsonElement<?> element) {
		if (element.isJsonObject()) {
			com.google.gson.JsonObject obj = element.asJsonObject().getJsonElement();

			obj.entrySet().removeIf(entry -> entry.getValue().isJsonNull());
			obj.entrySet().forEach(entry -> removeNulls(new JsonElement<>(entry.getValue())));
		}
		else if (element.isJsonArray()) {
			JsonArray arr = element.asJsonArray();
			removeIf(arr, JsonElement::isJsonNull);
			arr.forEach(JsonUtils::removeNulls);
		}
	}

	public static void removeIf(JsonArray arr, Predicate<JsonElement<?>> condition) {
		for (int i = arr.size() - 1; i >= 0; i--) {
			JsonElement<?> e = arr.get(i);
			if (condition.test(e)) {
				arr.remove(i);
			}
		}
	}
}
