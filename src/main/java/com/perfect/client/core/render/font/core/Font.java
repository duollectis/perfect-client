package com.perfect.client.core.render.font.core;

import com.perfect.client.core.render.font.Fonts;
import com.perfect.client.core.render.font.utils.FontUtils;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.render.texture.LoadedTexture;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.files.json.JsonUtils;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.utils.Align;
import lombok.Getter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.Map;

import static com.perfect.client.PerfectClient.TEXTURE_LOADER;

public class Font {

	public static final float FONT_HARDNESS = 1.5f;

	@Getter
	private final LoadedTexture atlas;
	private final Map<Integer, Glyph> glyphs;
	private final Metrics metrics;

	public Font(String path) {
		atlas = TEXTURE_LOADER.loadTexture(path + "/atlas.png");
		JsonObject json = JsonUtils.getResourceJson(path + "/metrics.json");

		if (json == null) {
			throw new NullPointerException();
		}

		metrics = FontUtils.parseMetrics(json);
		glyphs = FontUtils.parseGlyphs(json);
	}

	
	public void drawXCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, Color color) {
		draw(matrices, text, fontSize, x - getTextWidth(text, fontSize) / 2f, y, color);
	}

	
	public void drawXCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, MultiColor color, float baseOffset, float spread) {
		draw(matrices, text, fontSize, x - getTextWidth(text, fontSize) / 2f, y, color, baseOffset, spread);
	}

	
	public void drawYCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, Color color) {
		draw(matrices, text, fontSize, x, y - getFontHeight(fontSize) / 2f, color);
	}

	
	public void drawYCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, MultiColor color, float baseOffset, float spread) {
		draw(matrices, text, fontSize, x, y - getFontHeight(fontSize) / 2f, color, baseOffset, spread);
	}

	
	public void drawCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, Color color) {
		draw(matrices, text, fontSize, x - getTextWidth(text, fontSize) / 2f, y - getFontHeight(fontSize) / 2f, color);
	}

	
	public void drawCentered(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, MultiColor color, float baseOffset, float spread) {
		draw(matrices, text, fontSize, x - getTextWidth(text, fontSize) / 2f, y - getFontHeight(fontSize) / 2f, color, baseOffset, spread);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, float parentWidth, float parentHeight, Align xAlign, Align yAlign, Color color) {
		float drawX = xAlign.align(x, parentWidth, () -> getTextWidth(text, fontSize));
		float drawY = yAlign.align(y, parentHeight, () -> getFontHeight(fontSize));

		draw(matrices, text, fontSize, drawX, drawY, color);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, float parentWidth, float parentHeight, Align xAlign, Align yAlign, MultiColor color, float baseOffset, float spread) {
		float drawX = xAlign.align(x, parentWidth, () -> getTextWidth(text, fontSize));
		float drawY = yAlign.align(y, parentHeight, () -> getFontHeight(fontSize));

		draw(matrices, text, fontSize, drawX, drawY, color, baseOffset, spread);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, Color color) {
		drawCore(matrices, text, fontSize, x, y, color, null, 0f, 0f);
	}

	
	public void draw(Matrix3x2fStack matrices, String text, float fontSize, float x, float y, MultiColor color, float baseOffset, float spread) {
		drawCore(matrices, text, fontSize, x, y, null, color, baseOffset, spread);
	}

	
	private void drawCore(
			Matrix3x2fStack matrices,
			String text,
			float fontSize,
			float x,
			float y,
			Color singleColor,
			MultiColor multiColor,
			float baseOffset,
			float spread
	) {
		float baseX = x;
		VertexBuilder builder = getVertexBuilder();

		for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);
			Glyph glyph = glyphs.get((int) c);

			if (glyph == null) {
				continue;
			}

			Color charColor = singleColor;

			if (multiColor != null) {
				charColor = multiColor.getOffsetColor(baseOffset + (index * spread));
			}

			glyph.draw(matrices, builder, metrics, baseX, y, fontSize, charColor);

			baseX += glyph.advance() * fontSize;
		}

		if (!Fonts.isPacking()) {
			Fonts.render(this, builder);
		}
	}

	
	private VertexBuilder getVertexBuilder() {
		if (Fonts.isPacking()) {
			return Fonts.getPacketBuilder();
		} else {
			return new VertexBuilder(VertexFormats.POSITION_TEXTURE_COLOR, VertexDrawMode.QUADS);
		}
	}

	
	public float getTextWidth(String text, float size) {
		float width = 0f;
		for (char c : text.toCharArray()) {
			width += getCharWidth(c, size);
		}
		return width;
	}

	
	public float getCharWidth(char c, float size) {
		Glyph glyph = glyphs.get((int) c);
		if (glyph == null) {
			return 0f;
		}
		return glyph.advance() * size;
	}

	
	public float getFontHeight(float size) {
		return metrics.lineHeight() * size;
	}
}