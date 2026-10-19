package com.perfect.client.core.render.shader.renderer;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.render.texture.LoadedTexture;
import com.perfect.client.core.utils.color.ColorUtils;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.awt.*;


public class SdfShader extends Shader {

	public SdfShader() {
		super(VertexFormats.POSITION_TEXTURE, VertexDrawMode.QUADS);
	}

	public void drawXCentered(
			Matrix3x2fStack matrices,
			LoadedTexture texture,
			float x, float y, float size,
			float smoothing,
			Color color
	) {
		draw(matrices, texture, x - size / 2f, y, size, smoothing, color);
	}

	public void drawYCentered(
			Matrix3x2fStack matrices,
			LoadedTexture texture,
			float x, float y, float size,
			float smoothing,
			Color color
	) {
		draw(matrices, texture, x, y - size / 2f, size, smoothing, color);
	}

	public void drawCentered(
			Matrix3x2fStack matrices,
			LoadedTexture texture,
			float x, float y, float size,
			float smoothing,
			Color color
	) {
		draw(matrices, texture, x - size / 2f, y - size / 2f, size, smoothing, color);
	}

	public void draw(
			Matrix3x2fStack matrices,
			LoadedTexture texture,
			float x, float y, float size,
			float smoothing,
			Color color
	) {
		shaderProgram.setTexture("sdfTexture", texture.getGlId(), 0);
		shaderProgram.setUniform("uSmoothing", smoothing);

		Color c = ColorUtils.multiplyAlpha(color, Renderer2D.getDrawOpacity());
		shaderProgram.setUniform("uColor", ColorUtils.getArray4f(c));

		Matrix3x2f matrix = new Matrix3x2f(matrices);

		Renderer2D.preRender();

		float x1 = x + size;
		float y1 = y + size;

		VertexBuilder vertexBuilder = getVertexBuilder();

		vertexBuilder.pos(matrix, x, y).tex(0, 0);
		vertexBuilder.pos(matrix, x1, y).tex(1, 0);
		vertexBuilder.pos(matrix, x1, y1).tex(1, 1);
		vertexBuilder.pos(matrix, x, y1).tex(0, 1);

		vertexBuilder.draw(shaderProgram);

		Renderer2D.postRender();
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D sdfTexture;
				uniform vec4 uColor;
				uniform float uSmoothing;
				
				in vec2 texCoord;
				out vec4 fragColor;
				
				void main() {
				    float distance = texture(sdfTexture, texCoord).r;
				
				    float alpha = smoothstep(0.5 - uSmoothing, 0.5 + uSmoothing, distance);
				
				    fragColor = vec4(uColor.rgb, uColor.a * alpha);
				}
				""";
	}
}
