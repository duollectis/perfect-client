package com.perfect.client.core.utils.functions;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.Collections;
import java.util.List;

public class FunctionUtils {

	public static MutableText getErrorText(String name, String error) {
		MutableText text = Text.empty();
		text.append("[" + name + "] ");

		return text.append(Text.literal(error).setStyle(text.getStyle().withColor(Formatting.DARK_RED)));
	}

	public static MutableText getInfoText(String name, String info) {
		MutableText text = Text.empty();
		text.append("[" + name + "] ");

		String[] array = info.split("");
		List<Color> colors = ColorUtils.createGradient(
				array.length,
				Collections.singletonList(Managers.COLOR.get(Managers.COLOR.MAIN))
		);

		int i = 0;
		for (String s : array) {
			text.append(Text.literal(s).setStyle(text.getStyle().withColor(colors.get(i).getRGB())));
			i++;
		}

		return text;
	}

	public static MutableText getToggledText(String name, boolean enabled) {
		MutableText text = Text.empty();
		text.append(name + " ");

		String[] prefix = "has been".split("");
		String toggled = enabled ? "enabled" : "disabled";
		Color toggledColor = enabled ? new Color(69, 255, 27) : new Color(255, 0, 68);

		List<Color> colors = ColorUtils.createGradient(
				prefix.length,
				Collections.singletonList(Managers.COLOR.get(Managers.COLOR.MAIN))
		);

		int i = 0;
		for (String s : prefix) {
			text.append(Text.literal(s).setStyle(text.getStyle().withColor(colors.get(i).getRGB())));
			i++;
		}

		text.append(" ");

		text.append(Text.literal(toggled).setStyle(text.getStyle().withColor(toggledColor.getRGB())));
		return text;
	}
}
