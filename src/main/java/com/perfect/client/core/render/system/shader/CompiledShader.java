package com.perfect.client.core.render.system.shader;

import lombok.Getter;

import static org.lwjgl.opengl.GL40.*;

@Getter
public class CompiledShader {

	private final int programId;
	private final int vertexShaderId;
	private final int fragmentShaderId;

	public CompiledShader(String vertexSource, String fragmentSource) {
		programId = glCreateProgram();
		vertexShaderId = loadShader(vertexSource, GL_VERTEX_SHADER);
		fragmentShaderId = loadShader(fragmentSource, GL_FRAGMENT_SHADER);

		glAttachShader(programId, vertexShaderId);
		glAttachShader(programId, fragmentShaderId);

		glLinkProgram(programId);
		glValidateProgram(programId);

		if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
			throw new RuntimeException("Shader program linking failed: %s".formatted(glGetProgramInfoLog(programId)));
		}
	}

	
	public void use() {
		glUseProgram(programId);
	}

	private int loadShader(String source, int type) {
		int shaderId = glCreateShader(type);
		glShaderSource(shaderId, source);
		glCompileShader(shaderId);

		if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
			throw new RuntimeException("%s compilation failed: %s".formatted(
					getTypeName(type),
					glGetShaderInfoLog(shaderId)
			));
		}

		return shaderId;
	}

	private String getTypeName(int type) {
		return switch (type) {
			case GL_VERTEX_SHADER -> "Vertex shader";
			case GL_FRAGMENT_SHADER -> "Fragment shader";
			default -> "Shader";
		};
	}
}
