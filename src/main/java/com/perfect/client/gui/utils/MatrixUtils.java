package com.perfect.client.gui.utils;

import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

import static com.perfect.client.PerfectClient.mc;


public class MatrixUtils {

	public static Point scale(Matrix3x2fStack matrices, float x, float y) {
		return scale(new Matrix3x2f(matrices), x, y);
	}

	public static Point scale(Matrix3x2f matrix, float x, float y) {
		Vector2f pos = matrix.transformPosition(x, y, new Vector2f());
		return new Point(pos.x, pos.y);
	}

	public static float getFinalScale(Matrix3x2f matrix) {
		return (float) mc.getWindow().getScaleFactor() * getMatrixScaleFactor(matrix);
	}

	public static float getMatrixScaleFactor(Matrix3x2f matrix) {
		return (matrix.m00 + matrix.m11) / 2.0f;
	}

	public static Point revertScale(Matrix3x2fStack matrices, float x, float y) {
		Matrix3x2f inverse = new Matrix3x2f(matrices).invert();
		Vector2f pos = inverse.transformPosition(x, y, new Vector2f());
		return new Point(pos.x, pos.y);
	}
}
