package com.perfect.client.gui.utils;

import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WindowSerialize implements ISerializable<WindowSerialize> {

	@Getter
	private final String id;

	private boolean active;

	private float x;
	private float y;

	public WindowSerialize(String id) {
		this.id = id;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("enabled", active);
		json.add("x", x);
		json.add("y", y);

		return json;
	}

	@Override
	public WindowSerialize fromJson(JsonObject json) {
		active = json.getBoolean("enabled");
		x = json.getFloat("x");
		y = json.getFloat("y");
		return this;
	}

	public void reset() {
		active = false;
	}
}
