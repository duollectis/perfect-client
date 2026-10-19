package com.perfect.client.functional.function.visuals.nametags.utils;

import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.awt.*;

public class NametagsUtils {

	public static void addFormattedDisplayName(Entity entity, ColoredString coloredString) {
		if (entity instanceof PlayerEntity player) {
			Team team = getTeam(player);

			if (team != null) {
				addText(team.getPrefix(), coloredString);
			}

			coloredString.add(player.getGameProfile().getName(), RenderUtils.getEntityColor(entity, Color.WHITE));

			if (team != null) {
				addText(team.getSuffix(), coloredString);
			}
		}
		else {
			Text displayName = entity.getDisplayName();

			if (displayName != null) {
				coloredString.add(stripFormatting(displayName.getString()), RenderUtils.getEntityColor(entity, Color.WHITE));
			}
		}
	}

	private static void addText(Text text, ColoredString coloredString) {
		if (text == null) {
			return;
		}

		coloredString.add(stripFormatting(text.getString()), extractColor(text));
	}

	private static Team getTeam(PlayerEntity player) {
		return player.getScoreboard().getScoreHolderTeam(player.getGameProfile().getName());
	}

	private static Color extractColor(Text text) {
		Color directColor = resolveColor(text);

		if (directColor != Color.WHITE) {
			return directColor;
		}

		for (Text sibling : text.getSiblings()) {
			Color siblingColor = resolveColor(sibling);

			if (siblingColor != Color.WHITE) {
				return siblingColor;
			}
		}

		return Color.WHITE;
	}

	private static Color resolveColor(Text text) {
		TextColor color = text.getStyle().getColor();

		if (color != null) {
			return new Color(color.getRgb());
		}

		return Color.WHITE;
	}

	private static String stripFormatting(String input) {
		StringBuilder result = new StringBuilder(input.length());

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			if (c == '§' && i + 1 < input.length()) {
				i++;
				continue;
			}

			result.append(c);
		}

		return result.toString();
	}
}
