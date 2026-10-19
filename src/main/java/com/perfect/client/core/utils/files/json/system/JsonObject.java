package com.perfect.client.core.utils.files.json.system;

import java.util.Set;

public class JsonObject extends JsonElement<com.google.gson.JsonObject> {

	public JsonObject() {
		this(new com.google.gson.JsonObject());
	}

	public JsonObject(com.google.gson.JsonObject jsonObject) {
		super(jsonObject);
	}

	public Set<String> keySet() {
		return jsonElement.keySet();
	}

	public boolean has(String property) {
		return jsonElement.has(property);
	}

	public void add(String property, JsonElement<?> value) {
		if (value != null) {
			jsonElement.add(property, value.jsonElement);
		}
	}

	public void add(String property, String value) {
		if (value != null) {
			jsonElement.addProperty(property, value);
		}
	}

	public void add(String property, boolean value) {
		jsonElement.addProperty(property, value);
	}

	public void add(String property, Number value) {
		jsonElement.addProperty(property, value);
	}

	public JsonElement<?> get(String property) {
		return tryOrNull(() -> new JsonElement<>(jsonElement.get(property)));
	}

	public JsonObject getJsonObject(String property) {
		return tryOrDefault(() -> new JsonObject(jsonElement.getAsJsonObject(property)), new JsonObject());
	}

	public JsonArray getJsonArray(String property) {
		return tryOrDefault(() -> new JsonArray(jsonElement.getAsJsonArray(property)), new JsonArray());
	}

	public boolean getBoolean(String property) {
		return getBoolean(property, false);
	}

	public boolean getBoolean(String property, boolean defaultValue) {
		return tryOrDefault(() -> jsonElement.get(property).getAsBoolean(), defaultValue);
	}

	public String getString(String property) {
		return getString(property, null);
	}

	public String getString(String property, String defaultValue) {
		return tryOrDefault(() -> jsonElement.get(property).getAsString(), defaultValue);
	}

	public int getInt(String property) {
		return getInt(property, 0);
	}

	public int getInt(String property, int defaultValue) {
		return tryOrDefault(() -> jsonElement.get(property).getAsInt(), defaultValue);
	}

	public float getFloat(String property) {
		return getFloat(property, 0.0f);
	}

	public float getFloat(String property, float defaultValue) {
		return tryOrDefault(() -> jsonElement.get(property).getAsFloat(), defaultValue);
	}

	public double getDouble(String property) {
		return getDouble(property, 0.0);
	}

	public double getDouble(String property, double defaultValue) {
		return tryOrDefault(() -> jsonElement.get(property).getAsDouble(), defaultValue);
	}
}
