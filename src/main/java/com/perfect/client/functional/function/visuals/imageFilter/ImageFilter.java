package com.perfect.client.functional.function.visuals.imageFilter;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;

import java.awt.*;

public class ImageFilter extends Function {

	private final ImageFilterConfig config = new ImageFilterConfig(this);

	public ImageFilter() {
		super("image-filter", "Image Filter", Category.VISUALS, "Full Bright", "Hue", "NightMode");
	}

	public Color getColor() {
		return config.getColor().getColor();
	}
}
