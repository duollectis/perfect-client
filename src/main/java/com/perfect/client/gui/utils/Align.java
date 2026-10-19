package com.perfect.client.gui.utils;

import lombok.AllArgsConstructor;

import java.util.function.Supplier;

@AllArgsConstructor
public enum Align {
	MIN((min, max, size) -> min),
	MAX((min, parentSize, size) -> min + parentSize - size.get()),
	CENTER((min, parentSize, size) -> min + parentSize / 2.0f - size.get() / 2.0f);

	private final IAlign function;

	public float align(float min, float parentSize, Supplier<Float> size) {
		return function.align(min, parentSize, size);
	}

	private interface IAlign {

		float align(float min, float parentSize, Supplier<Float> size);
	}
}
