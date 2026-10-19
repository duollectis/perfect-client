package com.perfect.client.core.render.box.pattern;

import com.perfect.client.core.render.box.BoxColors;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import org.joml.Matrix4f;

public interface IPattern {

	void render(
			VertexBuilder builder, Matrix4f matrix,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			BoxColors colors
	);
}
