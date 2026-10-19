package com.perfect.client.functional.function.visuals.nametags.utils;

import com.perfect.client.core.render.RenderPackets;
import com.perfect.client.core.render.font.Fonts;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;


public class ColoredString {

	private final List<ColoredPart> parts = new ArrayList<>();

	public void add(String string, Color color) {
		parts.add(new ColoredPart(string, color));
	}

	public float getWidth() {
		float width = 0;
		for (ColoredPart part : parts) {
			width += MONTSERRAT_MEDIUM.getTextWidth(part.string(), 3.5f);
		}
		return width;
	}

	public void draw(Matrix3x2fStack matrices, float x, float y, float width, float height) {
		float textWidth = getWidth();

		float startX = x + width / 2f - textWidth / 2f;

		float _width = 0;

		Fonts.enablePacking(RenderPackets.NAMETAGS_FONT_LAYER);

		for (ColoredPart part : parts) {

			MONTSERRAT_MEDIUM.drawYCentered(
					matrices,
					part.string(),
					3.5f,
					startX + _width,
					y + height / 2f,
					part.color()
			);

			_width += MONTSERRAT_MEDIUM.getTextWidth(part.string(), 3.5f);
		}

		Fonts.disablePacking();
		Fonts.render(MONTSERRAT_MEDIUM, RenderPackets.NAMETAGS_FONT_LAYER);
	}

	public record ColoredPart(
			String string,
			Color color
	) {
	}
}