package com.perfect.client.core.render.animation.world.scan.direction;

import com.perfect.client.core.render.animation.world.scan.FloatBox;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.Color4f;
import org.joml.Matrix4f;

public abstract class DrawDirection {

	protected final boolean positive;

	public DrawDirection(boolean positive) {
		this.positive = positive;
	}

	// positionBox используется для расчета позиции по главной оси,
	// где главная ось, это ось параллельная Direction.
	//
	// sizeBox используется для расчета размером по второстепенным осям.
	// Площадь отрисовки перпендикулярна Direction.
	//
	// Direction стоят в парах (UP-DOWN, EAST-WEST, SOUTH-NORTH) по 1 для каждой оси.
	// Чтобы различать Direction, используется флаг positive

	public abstract void draw(
			Matrix4f matrix,
			FloatBox positionBox,
			FloatBox sizeBox,
			float offset,
			VertexBuilder faceBuilder, VertexBuilder lineBuilder,
			Color4f lineColor, Color4f sideColor
	);

	protected float signOffset(float offset) {
		return offset * (positive ? 1.0f : -1.0f);
	}
}
