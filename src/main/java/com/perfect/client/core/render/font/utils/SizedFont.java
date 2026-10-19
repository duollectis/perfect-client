package com.perfect.client.core.render.font.utils;

import com.perfect.client.core.render.font.core.Font;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.gui.utils.Align;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class SizedFont {

	private final Supplier<Font> font;
	private final float size;

	public SizedFont(Supplier<Font> font, float size) {
		this.font = font;
		this.size = size;
	}

	
	public void drawXCentered(Matrix3x2fStack matrices, String text, float x, float y, Color color) {
		font.get().drawXCentered(matrices, text, size, x, y, color);
	}

	
	public void drawYCentered(Matrix3x2fStack matrices, String text, float x, float y, Color color) {
		font.get().drawYCentered(matrices, text, size, x, y, color);
	}

	
	public void drawCentered(Matrix3x2fStack matrices, String text, float x, float y, Color color) {
		font.get().drawCentered(matrices, text, size, x, y, color);
	}

	
	public void draw(
			Matrix3x2fStack matrices,
			String text,
			float x,
			float y,
			float parentWidth,
			float parentHeight,
			Align xAlign,
			Align yAlign,
			Color color
	) {
		font.get().draw(matrices, text, size, x, y, parentWidth, parentHeight, xAlign, yAlign, color);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float x, float y, Color color) {
		font.get().draw(matrices, text, size, x, y, color);
	}

	
	public void drawXCentered(Matrix3x2fStack matrices, String text, float x, float y, MultiColor color, float baseOffset, float spread) {
		font.get().drawXCentered(matrices, text, size, x, y, color, baseOffset, spread);
	}

	
	public void drawYCentered(Matrix3x2fStack matrices, String text, float x, float y, MultiColor color, float baseOffset, float spread) {
		font.get().drawYCentered(matrices, text, size, x, y, color, baseOffset, spread);
	}

	
	public void drawCentered(Matrix3x2fStack matrices, String text, float x, float y, MultiColor color, float baseOffset, float spread) {
		font.get().drawCentered(matrices, text, size, x, y, color, baseOffset, spread);
	}

	
	public void draw(
			Matrix3x2fStack matrices,
			String text,
			float x,
			float y,
			float parentWidth,
			float parentHeight,
			Align xAlign,
			Align yAlign,
			MultiColor color,
			float baseOffset,
			float spread
	) {
		font.get().draw(matrices, text, size, x, y, parentWidth, parentHeight, xAlign, yAlign, color, baseOffset, spread);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float x, float y, MultiColor color, float baseOffset, float spread) {
		font.get().draw(matrices, text, size, x, y, color, baseOffset, spread);
	}

	
	public float getTextWidth(String text) {
		return font.get().getTextWidth(text, size);
	}

	
	public float getCharWidth(char c) {
		return font.get().getCharWidth(c, size);
	}

	
	public float getFontHeight() {
		return font.get().getFontHeight(size);
	}
}