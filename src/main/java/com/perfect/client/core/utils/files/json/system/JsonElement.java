package com.perfect.client.core.utils.files.json.system;

import com.google.gson.JsonNull;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class JsonElement<T extends com.google.gson.JsonElement> {

	protected final T jsonElement;

	public JsonElement(T jsonElement) {
		this.jsonElement = jsonElement;
	}

	public boolean isJsonNull() {
		return jsonElement instanceof JsonNull;
	}

	public boolean isJsonArray() {
		return jsonElement instanceof com.google.gson.JsonArray;
	}

	public boolean isJsonObject() {
		return jsonElement instanceof com.google.gson.JsonObject;
	}

	public JsonObject asJsonObject() {
		return tryOrDefault(() -> new JsonObject(jsonElement.getAsJsonObject()), new JsonObject());
	}

	public JsonArray asJsonArray() {
		return tryOrDefault(() -> new JsonArray(jsonElement.getAsJsonArray()), new JsonArray());
	}

	public boolean asBoolean() {
		return tryOrDefault(jsonElement::getAsBoolean, false);
	}

	public String asString() {
		return tryOrDefault(jsonElement::getAsString, null);
	}

	public int asInt() {
		return tryOrDefault(jsonElement::getAsInt, 0);
	}

	public float asFloat() {
		return tryOrDefault(jsonElement::getAsFloat, 0.0f);
	}

	public double asDouble() {
		return tryOrDefault(jsonElement::getAsDouble, 0.0);
	}

	public <B> B tryOrNull(Supplier<B> supplier) {
		return tryOrDefault(supplier, null);
	}

	public <B> B tryOrDefault(Supplier<B> supplier, B defaultValue) {
		try {
			return supplier.get();
		}
		catch (Exception exception) {
			return defaultValue;
		}
	}
}
