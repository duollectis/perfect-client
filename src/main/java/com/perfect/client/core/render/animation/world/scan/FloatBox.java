package com.perfect.client.core.render.animation.world.scan;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FloatBox {

	public float x1;
	public float y1;
	public float z1;
	public float x2;
	public float y2;
	public float z2;

	public float getX(boolean positive) {
		if (positive) {
			return x2;
		}
		else {
			return x1;
		}
	}

	public float getY(boolean positive) {
		if (positive) {
			return y2;
		}
		else {
			return y1;
		}
	}

	public float getZ(boolean positive) {
		if (positive) {
			return z2;
		}
		else {
			return z1;
		}
	}

	public FloatBox expand(float expand) {
		return new FloatBox(
				x1 - expand,
				y1 - expand,
				z1 - expand,
				x2 + expand * 2f,
				y2 + expand * 2f,
				z2 + expand * 2f
		);
	}
}