package com.perfect.client.core.render.renderer.buffer;

import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormat;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class RenderBuffer {

	private final VertexBuilder vertexBuilder;

	public RenderBuffer(VertexFormat format, VertexDrawMode drawMode) {
		vertexBuilder = new VertexBuilder(format, drawMode);
	}

	public void draw(Consumer<VertexBuilder> drawAction) {
		draw(drawAction, true);
	}

	public void draw(Consumer<VertexBuilder> drawAction, boolean clear) {
		drawAction.accept(vertexBuilder);

		if (clear) {
			vertexBuilder.clear();
		}
	}

	public boolean isEmpty() {
		return vertexBuilder.isEmpty();
	}
}
