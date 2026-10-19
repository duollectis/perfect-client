package com.perfect.client.core.render.shader.renderer;

import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;


public class FontShader extends Shader {

	public FontShader() {
		super(VertexFormats.POSITION_TEXTURE_COLOR, VertexDrawMode.QUADS);
	}

	public void draw(VertexBuilder builder, int texture, float hardness) {
		shaderProgram.setTexture("Sampler0", texture, 0);
		shaderProgram.setUniform("hardness", hardness);

		Renderer2D.preRender();

		Render.blendFuncSeparate(
				SrcFactor.SRC_ALPHA,
				DstFactor.ONE_MINUS_SRC_ALPHA,
				SrcFactor.ONE,
				DstFactor.ZERO
		);

		builder.draw(shaderProgram);

		Renderer2D.postRender();
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D Sampler0;
				
				uniform float hardness;
				
				in vec2 texCoord;
				in vec4 vertexColor;
				
				out vec4 fragColor;
				
				float median(float r, float g, float b) {
				    return max(min(r, g), min(max(r, g), b));
				}
				
				float screenPxRange() {
				    vec2 unitRange = vec2(hardness)/vec2(textureSize(Sampler0, 0));
				    vec2 screenTexSize = vec2(1.0)/fwidth(texCoord);
				    return max(0.5*dot(unitRange, screenTexSize), 1.0);
				}
				
				void main() {
				    vec3 msd = texture(Sampler0, texCoord).rgb;
				    float sd = median(msd.r, msd.g, msd.b);
				
				    float screenPxDistance = screenPxRange() * (sd - 0.5);
				    float opacity = clamp(screenPxDistance + 0.5, 0.0, 1.0);
				
				    fragColor = vec4(vertexColor.rgb, vertexColor.a * opacity);
				}
				""";
	}
}
