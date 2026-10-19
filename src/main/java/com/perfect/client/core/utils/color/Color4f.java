package com.perfect.client.core.utils.color;

import lombok.AllArgsConstructor;
import org.joml.Vector4f;

import java.awt.*;

@AllArgsConstructor

public class Color4f {

	public final float r, g, b, a;

	public Color4f(Color col) {
		float[] col4f = ColorUtils.getArray4f(col);

		r = col4f[0];
		g = col4f[1];
		b = col4f[2];
		a = col4f[3];
	}

	public Color4f withAlpha(float a) {
		return new Color4f(r, g, b, a);
	}

	public Vector4f translateToVec4f() {
		return new Vector4f(r, g, b, a);
	}
}
