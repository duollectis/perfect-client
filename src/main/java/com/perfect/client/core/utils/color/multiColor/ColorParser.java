package com.perfect.client.core.utils.color.multiColor;

import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.text.StringUtils;

public class ColorParser {

	private final MultiColor color;

	public ColorParser(MultiColor color) {
		this.color = color;
	}

	public String asString() {
		StringBuilder stringBuilder = new StringBuilder();

		for (MutableColor color : color.getColorList()) {
			String colorString = ColorUtils.colorToHex(color.getColor());
			stringBuilder.append(colorString);
			stringBuilder.append(", ");
		}

		if (!stringBuilder.isEmpty()) {
			stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		}

		stringBuilder.append("; %s; %s".formatted(color.getAlpha(), color.getSpeed()));

		return stringBuilder.toString();
	}

	public void parse(String string) {
		String[] strings = string.replaceAll(" ", "").split(";");

		if (strings.length != 3) {
			return;
		}

		color.getColorList().clear();

		String[] colors = strings[0].split(",");

		for (String col : colors) {
			ColorUtils.parseHexColor(col).ifPresent(color::add);
		}

		color.setAlpha(StringUtils.parseFloat(strings[1]).orElse(0.0f));
		color.setSpeed(StringUtils.parseFloat(strings[2]).orElse(0.0f));
	}
}

