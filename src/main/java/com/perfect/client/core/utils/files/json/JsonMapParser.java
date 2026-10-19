package com.perfect.client.core.utils.files.json;

import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.text.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonMapParser {

	public static Map<String, String> parse(String path) {
		JsonObject jsonObject = JsonUtils.getResourceJson(path);

		if (jsonObject == null) {
			return null;
		}

		return parseRecursive(jsonObject);
	}

	private static Map<String, String> parseRecursive(JsonObject object) {
		return parseRecursive(object, new HashMap<>(), new ArrayList<>());
	}

	private static Map<String, String> parseRecursive(
			JsonObject object,
			Map<String, String> parsed,
			List<String> path
	) {
		for (String key : object.keySet()) {
			path.add(key);

			JsonElement<?> element = object.get(key);

			if (element instanceof JsonObject jsonObject) {
				parseRecursive(jsonObject, parsed, path);
			}
			else {
				StringBuilder builder = new StringBuilder();
				path.forEach(k -> builder.append(k).append("."));

				int lastIndex = builder.length() - 1;
				if (lastIndex >= 0 && builder.charAt(lastIndex) == '.') {
					builder.deleteCharAt(lastIndex);
				}

				parsed.put(builder.toString(), StringUtils.openQuotes(String.valueOf(element)));
			}

			path.removeLast();
		}

		return parsed;
	}
}
