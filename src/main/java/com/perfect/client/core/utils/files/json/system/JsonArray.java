package com.perfect.client.core.utils.files.json.system;

import com.perfect.client.core.utils.files.ISerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class JsonArray extends JsonElement<com.google.gson.JsonArray> implements Iterable<JsonElement<com.google.gson.JsonElement>> {

	public JsonArray() {
		this(new com.google.gson.JsonArray());
	}

	public JsonArray(com.google.gson.JsonArray jsonElement) {
		super(jsonElement);
	}

	public void add(String value) {
		jsonElement.add(value);
	}

	public void add(Number value) {
		jsonElement.add(value);
	}

	public void add(boolean value) {
		jsonElement.add(value);
	}

	public void add(JsonElement<?> value) {
		jsonElement.add(value.jsonElement);
	}

	public JsonElement<?> remove(int i) {
		return new JsonElement<>(jsonElement.remove(i));
	}

	public int size() {
		return jsonElement.size();
	}

	public JsonElement<?> get(int i) {
		return new JsonElement<>(jsonElement.get(i));
	}

	@Override
	public @NotNull Iterator<JsonElement<com.google.gson.JsonElement>> iterator() {

		return new Iterator<>() {
			private final Iterator<com.google.gson.JsonElement> iterator = jsonElement.iterator();

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public JsonElement<com.google.gson.JsonElement> next() {
				return new JsonElement<>(iterator.next());
			}
		};
	}

	public static <T extends ISerializable<?>> JsonArray of(Iterable<T> list) {
		JsonArray json = new JsonArray();

		for (T item : list) {
			JsonObject jsonObject = item.toJson();

			if (jsonObject != null) {
				json.add(jsonObject);
			}
		}

		return json;
	}
}
