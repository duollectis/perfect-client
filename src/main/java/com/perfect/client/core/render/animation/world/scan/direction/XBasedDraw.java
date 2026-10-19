package com.perfect.client.core.render.animation.world.scan.direction;

import com.perfect.client.core.render.animation.world.scan.FloatBox;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.Color4f;
import org.joml.Matrix4f;

public class XBasedDraw extends DrawDirection {

	public XBasedDraw(boolean positive) {
		super(positive);
	}

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
		float pos = positionBox.getX(positive) + signOffset(offset);

		faceBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z1).color(sideColor);
		faceBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z1).color(sideColor);
		faceBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z2).color(sideColor);
		faceBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z2).color(sideColor);

		lineBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z1).color(lineColor);
		lineBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z1).color(lineColor);

		lineBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z1).color(lineColor);
		lineBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z2).color(lineColor);

		lineBuilder.pos(matrix, pos, sizeBox.y2, sizeBox.z2).color(lineColor);
		lineBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z2).color(lineColor);

		lineBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z2).color(lineColor);
		lineBuilder.pos(matrix, pos, sizeBox.y1, sizeBox.z1).color(lineColor);
	}
}
