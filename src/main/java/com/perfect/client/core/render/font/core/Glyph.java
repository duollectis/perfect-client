package com.perfect.client.core.render.font.core;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.ColorUtils;
import org.joml.Matrix3x2fStack;

import java.awt.*;


public record Glyph(
		float advance,
		Bounds planeBounds,
		Bounds atlasBounds
) {

	public void draw(
			Matrix3x2fStack matrices,
			VertexBuilder builder,
			Metrics metrics,
			float baseX,
			float baseY,
			float size,
			Color color
	) {
		if (planeBounds == null || atlasBounds == null) {
			return;
		}

		float ascender = metrics.ascender();

		float leftEm = planeBounds.left();
		float rightEm = planeBounds.right();
		float bottomEm = planeBounds.bottom();
		float topEm = planeBounds.top();

		float left = leftEm * size;
		float right = rightEm * size;
		float bottom = bottomEm * size;
		float top = topEm * size;

		float width = right - left;
		float height = top - bottom;

		float x = baseX + leftEm * size;
		float y = baseY + (ascender - topEm) * size;
		float x1 = x + width;
		float y1 = y + height;

		float aWidth = metrics.atlasWidth();
		float aHeight = metrics.atlasHeight();

		float u = atlasBounds.left() / aWidth;
		float u1 = atlasBounds.right() / aWidth;

		// Генератор атласа выдает странные корды, фикс
		float v = (aHeight - atlasBounds.top()) / aHeight;
		float v1 = (aHeight - atlasBounds.bottom()) / aHeight;

		Color c = ColorUtils.multiplyAlpha(color, Renderer2D.getDrawOpacity());

		builder.pos(matrices, x, y).tex(u, v).color(c);
		builder.pos(matrices, x1, y).tex(u1, v).color(c);
		builder.pos(matrices, x1, y1).tex(u1, v1).color(c);
		builder.pos(matrices, x, y1).tex(u, v1).color(c);
	}
}
