package com.perfect.client.core.utils;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Setter
@Getter
public class BlockColor implements ISerializable<BlockColor> {

	private Color color;

	private boolean set;
	private boolean enabled;

	public BlockColor enabled() {
		this.enabled = true;
		return this;
	}

	public void setColor(Color color) {
		set = true;
		this.color = color;
	}

	public Color getColor(Color defaultColor) {
		if (!set) {
			return defaultColor;
		}
		else {
			return color;
		}
	}

	public void toggleEnabled() {
		enabled = !enabled;
	}

	public void toggleSet() {
		if (!set) {
			this.color = Managers.COLOR.get(Managers.COLOR.MAIN);
		}

		set = !set;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("enabled", enabled);
		json.add("set", set);

		if (set) {
			json.add("r", color.getRed());
			json.add("g", color.getGreen());
			json.add("b", color.getBlue());
		}

		return json;
	}

	@Override
	public BlockColor fromJson(JsonObject json) {
		enabled = json.getBoolean("enabled");

		set = json.getBoolean("set");

		if (set) {
			int r = json.getInt("r");
			int g = json.getInt("g");
			int b = json.getInt("b");

			color = new Color(r, g, b);
		}

		return this;
	}
}
