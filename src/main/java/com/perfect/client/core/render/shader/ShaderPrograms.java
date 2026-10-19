package com.perfect.client.core.render.shader;

import com.perfect.client.core.render.system.shader.ShaderProgram;
import com.perfect.client.core.render.system.vertex.VertexSources;

public class ShaderPrograms {

	public static final ShaderProgram BLIT_SCREEN_LIGHT_MAP = new ShaderProgram(
			VertexSources.BLIT_SCREEN_LIGHT_MAP,
			ShaderSources.BLIT_SCREEN_LIGHT_MAP,
			true
	);

	public static final ShaderProgram BLIT_SCREEN = new ShaderProgram(
			VertexSources.BLIT_SCREEN,
			ShaderSources.BLIT_SCREEN,
			true
	);

	public static final ShaderProgram POSITION = new ShaderProgram(
			VertexSources.POSITION,
			ShaderSources.POSITION,
			true
	);

	public static final ShaderProgram POSITION_TEXTURE = new ShaderProgram(
			VertexSources.POSITION_TEXTURE,
			ShaderSources.POSITION_TEXTURE,
			true
	);

	public static final ShaderProgram POSITION_COLOR = new ShaderProgram(
			VertexSources.POSITION_COLOR,
			ShaderSources.POSITION_COLOR,
			true
	);

	public static final ShaderProgram POSITION_TEXTURE_COLOR = new ShaderProgram(
			VertexSources.POSITION_TEXTURE_COLOR,
			ShaderSources.POSITION_TEXTURE_COLOR,
			true
	);

	public static final ShaderProgram BRIGHT_POSITION_TEXTURE_COLOR = new ShaderProgram(
			VertexSources.POSITION_TEXTURE_COLOR,
			ShaderSources.BRIGHT_POSITION_TEXTURE_COLOR,
			true
	);
}
