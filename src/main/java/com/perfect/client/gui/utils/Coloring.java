package com.perfect.client.gui.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@Getter
@AllArgsConstructor
public class Coloring {

	private final Color topLeft;
	private final Color topRight;
	private final Color bottomLeft;
	private final Color bottomRight;

	public static Coloring solid(Color color) {
		return new Coloring(color, color, color, color);
	}

	public static Coloring horizontalGradient(Color left, Color right) {
		return new Coloring(left, right, left, right);
	}

	public static Coloring verticalGradient(Color top, Color bottom) {
		return new Coloring(top, top, bottom, bottom);
	}
}
