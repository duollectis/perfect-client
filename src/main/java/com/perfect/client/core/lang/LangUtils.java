package com.perfect.client.core.lang;

import com.perfect.client.PerfectClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LangUtils {

	public static Map<String, String> parseAsMap(String path) {
		Map<String, String> result = new HashMap<>();

		try {
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(Objects.requireNonNull(PerfectClient.getResource(path)), StandardCharsets.UTF_8))
			) {
				String line;

				while ((line = reader.readLine()) != null) {
					processLine(line, result);
				}
			}

			return result;
		}
		catch (IOException exception) {
			return null;
		}
	}

	private static void processLine(String line, Map<String, String> map) {
		String trimmed = line.trim();
		if (trimmed.isEmpty() || trimmed.startsWith("#")) {
			return;
		}

		int commentIndex = trimmed.indexOf(" #");
		if (commentIndex != -1) {
			trimmed = trimmed.substring(0, commentIndex).trim();
		}

		int equalsIndex = trimmed.indexOf('=');

		String key = trimmed.substring(0, equalsIndex).trim();
		String value = trimmed.substring(equalsIndex + 1).trim();

		if (!key.isEmpty()) {
			map.put(key, value);
		}
	}
}
