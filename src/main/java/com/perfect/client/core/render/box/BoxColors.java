package com.perfect.client.core.render.box;

import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.utils.color.Color4f;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;

import java.awt.*;


public class BoxColors {

	public final Color4f[] corners = new Color4f[8];

	private BoxColors(Color4f c0, Color4f c1, Color4f c2, Color4f c3, Color4f c4, Color4f c5, Color4f c6, Color4f c7) {
		corners[0] = c0;
		corners[1] = c1;
		corners[2] = c2;
		corners[3] = c3;
		corners[4] = c4;
		corners[5] = c5;
		corners[6] = c6;
		corners[7] = c7;
	}

	public static BoxColors plain(Color color) {
		Color4f c = new Color4f(color);

		return new BoxColors(c, c, c, c, c, c, c, c);
	}

	// имеется баг с генерацией цветов при segments = 7
	// а потому вместо слайдера сделал энум мод для их количества
	public static BoxColors multiGradient(Cosmetics cosmetics, MultiColor color, int segments) {
		segments = Math.clamp(segments, 2, 8);

		if (!cosmetics.isGradient() || segments == 2) {
			return gradient(cosmetics, color);
		}

		int colorCount = color.size();
		Color4f[] corners = new Color4f[8];

		for (int i = 0; i < 8; i++) {
			int segmentIndex;

			if (segments == 4) {
				int[] pairMapping = {0, 1, 2, 3, 2, 3, 0, 1};

				segmentIndex = pairMapping[i];
			} else {
				segmentIndex = (i * segments) / 8;
				segmentIndex = Math.min(segments, segmentIndex);
			}

			float colorT = (float) segmentIndex / segments;
			float colorOffset = colorT * (colorCount - 1);
			corners[i] = new Color4f(color.getOffsetColor(colorOffset));
		}

		return new BoxColors(
				corners[0], corners[1], corners[2], corners[3],
				corners[4], corners[5], corners[6], corners[7]
		);
	}

	public static BoxColors gradient(Cosmetics cosmetics, MultiColor color) {
		if (cosmetics.isGradient()) {
			float gradientOffset = 1f / color.size();
			return gradient(color.getColor(), color.getOffsetColor(gradientOffset));
		}
		else {
			return plain(color.getColor());
		}
	}

	public static BoxColors gradient(Color color1, Color color2) {
		Color4f c1 = new Color4f(color1);
		Color4f c2 = new Color4f(color2);

		return new BoxColors(c1, c1, c2, c1, c1, c2, c2, c2);
	}

	public static BoxColors horizontalTransparentGradient(Cosmetics cosmetics, MultiColor color, float a1, float a2) {
		if (cosmetics.isGradient()) {
			float gradientOffset = 1f / color.size();
			return horizontalTransparentGradient(color.getColor(), color.getOffsetColor(gradientOffset), a1, a2);
		}
		else {
			return horizontalTransparent(color.getColor(), a1, a2);
		}
	}

	public static BoxColors horizontalTransparentGradient(Color color1, Color color2, float a1, float a2) {
		Color4f c1_1 = new Color4f(ColorUtils.multiplyAlpha(color1, a1));
		Color4f c1_2 = new Color4f(ColorUtils.multiplyAlpha(color1, a2));

		Color4f c2_1 = new Color4f(ColorUtils.multiplyAlpha(color2, a1));
		Color4f c2_2 = new Color4f(ColorUtils.multiplyAlpha(color2, a2));

		return new BoxColors(c1_1, c1_1, c2_1, c1_1, c1_2, c2_2, c2_2, c2_2);
	}

	public static BoxColors horizontalTransparent(Color color, float a1, float a2) {
		Color4f c1 = new Color4f(ColorUtils.multiplyAlpha(color, a1));
		Color4f c2 = new Color4f(ColorUtils.multiplyAlpha(color, a2));

		return new BoxColors(c1, c1, c1, c1, c2, c2, c2, c2);
	}
}