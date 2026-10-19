package com.perfect.client.gui.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Rounding {

	private static final Rounding NONE = new Rounding(0.0f, 0.0f, 0.0f, 0.0f);

	private final float topLeft;
	private final float topRight;
	private final float bottomLeft;
	private final float bottomRight;

	public static Rounding full(float rounding) {
		return new Rounding(rounding, rounding, rounding, rounding);
	}

	public static Rounding left(float rounding) {
		return new Rounding(rounding, 0.0f, rounding, 0.0f);
	}

	public static Rounding right(float rounding) {
		return new Rounding(0.0f, rounding, 0.0f, rounding);
	}

	public static Rounding top(float rounding) {
		return new Rounding(rounding, rounding, 0.0f, 0.0f);
	}

	public static Rounding bottom(float rounding) {
		return new Rounding(0.0f, 0.0f, rounding, rounding);
	}

	public static Rounding none() {
		return NONE;
	}
}
