package com.perfect.client.core.render.shader.renderer.round;

import org.joml.Matrix3x2fStack;

public interface IBuiltDraw<T extends RoundBuilder<T>> {

	void draw(Matrix3x2fStack matrices, T builder);
}
