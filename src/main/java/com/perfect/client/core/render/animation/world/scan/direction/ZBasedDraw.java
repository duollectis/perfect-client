package com.perfect.client.core.render.animation.world.scan.direction;

import com.perfect.client.core.render.animation.world.scan.FloatBox;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.Color4f;
import org.joml.Matrix4f;

public class ZBasedDraw extends DrawDirection {

	public ZBasedDraw(boolean positive) {
		super(positive);
	}

	@Override
	public void draw(
			Matrix4f matrix,
			FloatBox positionBox,
			FloatBox sizeBox,
			float offset,
			VertexBuilder faceBuilder,
			VertexBuilder lineBuilder,
			Color4f lineColor,
			Color4f sideColor
	) {
		float pos = positionBox.getZ(positive) + signOffset(offset);

		faceBuilder.pos(matrix, sizeBox.x1, sizeBox.y1, pos).color(sideColor);
		faceBuilder.pos(matrix, sizeBox.x1, sizeBox.y2, pos).color(sideColor);
		faceBuilder.pos(matrix, sizeBox.x2, sizeBox.y2, pos).color(sideColor);
		faceBuilder.pos(matrix, sizeBox.x2, sizeBox.y1, pos).color(sideColor);

		lineBuilder.pos(matrix, sizeBox.x1, sizeBox.y1, pos).color(lineColor);
		lineBuilder.pos(matrix, sizeBox.x1, sizeBox.y2, pos).color(lineColor);

		lineBuilder.pos(matrix, sizeBox.x1, sizeBox.y2, pos).color(lineColor);
		lineBuilder.pos(matrix, sizeBox.x2, sizeBox.y2, pos).color(lineColor);

		lineBuilder.pos(matrix, sizeBox.x2, sizeBox.y2, pos).color(lineColor);
		lineBuilder.pos(matrix, sizeBox.x2, sizeBox.y1, pos).color(lineColor);

		lineBuilder.pos(matrix, sizeBox.x2, sizeBox.y1, pos).color(lineColor);
		lineBuilder.pos(matrix, sizeBox.x1, sizeBox.y1, pos).color(lineColor);
	}
}
