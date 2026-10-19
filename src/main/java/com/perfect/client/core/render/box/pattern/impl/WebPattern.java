package com.perfect.client.core.render.box.pattern.impl;

import com.perfect.client.core.render.box.BoxColors;
import com.perfect.client.core.render.box.pattern.IPattern;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import org.joml.Matrix4f;


public class WebPattern implements IPattern {

	@Override
	public void render(
			VertexBuilder builder, Matrix4f matrix,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			BoxColors colors
	) {
		builder.pos(matrix, x1, y1, z1).color(colors.corners[0]);
		builder.pos(matrix, x2, y2, z2).color(colors.corners[6]);

		builder.pos(matrix, x1, y1, z2).color(colors.corners[1]);
		builder.pos(matrix, x2, y2, z1).color(colors.corners[7]);

		builder.pos(matrix, x1, y2, z1).color(colors.corners[4]);
		builder.pos(matrix, x2, y1, z2).color(colors.corners[2]);

		builder.pos(matrix, x2, y1, z1).color(colors.corners[3]);
		builder.pos(matrix, x1, y2, z2).color(colors.corners[5]);
	}
}
