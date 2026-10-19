package com.perfect.client.core.utils.color.multiColor;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.IMutableColor;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;

import java.awt.*;


public class MutableColor implements IMutableColor, ISerializable<MutableColor> {

	private Color color;

	public MutableColor() {
		this(Color.WHITE);
	}

	public MutableColor(Color color) {
		this.color = color;
	}

	public MutableColor(MutableColor color) {
		this.color = color.getColor();
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public JsonObject toJson() {
		return ColorUtils.toJson(color);
	}

	@Override
	public MutableColor fromJson(JsonObject json) {
		color = ColorUtils.fromJson(json);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MutableColor mutableColor) {
			return color.equals(mutableColor.color);
		}
		return false;
	}
}
