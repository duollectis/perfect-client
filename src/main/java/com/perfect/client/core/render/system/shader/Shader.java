package com.perfect.client.core.render.system.shader;

import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormat;

public abstract class Shader {

	private final VertexBuilder vertexBuilder;

	protected final ShaderProgram shaderProgram;

	public Shader(VertexFormat vertexFormat, VertexDrawMode drawMode) {
		vertexBuilder = new VertexBuilder(vertexFormat, drawMode);
		shaderProgram = new ShaderProgram(vertexFormat.getVertexSource(), getFragmentSource(), false);
	}

	
	protected VertexBuilder getVertexBuilder() {
		vertexBuilder.clear();
		return vertexBuilder;
	}

	protected abstract String getFragmentSource();
}
