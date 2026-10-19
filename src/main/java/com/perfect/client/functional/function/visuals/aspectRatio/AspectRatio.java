package com.perfect.client.functional.function.visuals.aspectRatio;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;

public class AspectRatio extends Function {

	private final AspectRatioConfig config = new AspectRatioConfig(this);

	public AspectRatio() {
		super("aspect-ratio", "Aspect Ratio", Category.VISUALS);
	}

	public float getRatio() {
		return switch (config.getMode().get()) {
			case _16_9 -> 1.777f;
			case _16_10 -> 1.6f;
			case _4_3 -> 1.333f;
			case CUSTOM -> config.getValue().getFloat();
		};
	}

	public enum Mode {
		_16_9,
		_16_10,
		_4_3,
		CUSTOM
	}
}
