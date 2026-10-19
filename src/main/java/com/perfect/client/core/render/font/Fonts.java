package com.perfect.client.core.render.font;

import com.perfect.client.core.render.font.core.Font;
import com.perfect.client.core.render.font.core.type.FontInfo;
import com.perfect.client.core.render.font.core.type.FontStyle;
import com.perfect.client.core.render.font.core.type.FontType;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.gui.utils.Align;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Fonts {

	private static final String FONT_PATH = "fonts/";

	private static final Map<FontInfo, Font> fontMap = new HashMap<>();

	private static final BiConsumer<Font, VertexBuilder> renderer = (font, builder) -> Renderer2D.FONT_SHADER.draw(
			builder,
			font.getAtlas().getGlId(),
			Font.FONT_HARDNESS
	);

	@Setter
	private static BiConsumer<Font, VertexBuilder> customRenderer = null;

	public static Font MONTSERRAT_MEDIUM;
	public static Font MONTSERRAT_SEMI_BOLD;

	@Getter
	private static boolean packing;

	@Getter
	private static VertexBuilder packetBuilder;

	
	public static void init() {
		for (FontType fontType : FontType.values()) {
			for (FontStyle style : fontType.getStyles()) {

				FontInfo fontInfo = new FontInfo(fontType, style);
				Font font = createFont(fontInfo);

				fontMap.put(fontInfo, font);
			}
		}

		MONTSERRAT_MEDIUM = fontMap.get(new FontInfo(FontType.MONTSERRAT, FontStyle.MEDIUM));
		MONTSERRAT_SEMI_BOLD = fontMap.get(new FontInfo(FontType.MONTSERRAT, FontStyle.SEMI_BOLD));
	}

	
	private static Font createFont(FontInfo fontInfo) {
		return new Font("%s%s/%s".formatted(FONT_PATH, fontInfo.getFontType().getFileName(), fontInfo.getFontStyle().getFileName()));
	}

	public static void render(Font font, VertexBuilder builder) {
		Objects.requireNonNullElse(customRenderer, renderer).accept(font, builder);
		builder.clear();
	}

	public static void enablePacking(VertexBuilder builder) {
		packing = true;
		packetBuilder = builder;
	}

	public static void disablePacking() {
		packing = false;
	}

	
	public void draw(
			FontInfo font,
			Matrix3x2fStack matrices,
			String text,
			float fontSize,
			float x,
			float y,
			float parentWidth,
			float parentHeight,
			Align xAlign,
			Align yAlign,
			Color color
	) {
		fontMap.get(font).draw(matrices, text, fontSize, x, y, parentWidth, parentHeight, xAlign, yAlign, color);
	}

	
	public float getTextWidth(FontInfo font, float size, String text) {
		return fontMap.get(font).getTextWidth(text, size);
	}

	
	public float getCharWidth(FontInfo font, float size, char c) {
		return fontMap.get(font).getCharWidth(c, size);
	}

	
	public float getFontHeight(FontInfo font, float size) {
		return fontMap.get(font).getFontHeight(size);
	}
}
