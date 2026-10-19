package com.perfect.client.core.render.shader.renderer;

import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.mc;

public class BorderedFontShader extends Shader {

	public BorderedFontShader() {
		super(VertexFormats.POSITION_TEXTURE_COLOR, VertexDrawMode.QUADS);
	}

	public void draw(
			Matrix3x2fStack matrices,
			VertexBuilder builder,
			int texture,
			float borderEndX,
			float borderSize,
			float hardness
	) {
		float scale = (float) mc.getWindow().getScaleFactor();
		Matrix3x2f matrix = new Matrix3x2f(matrices);

		shaderProgram.setTexture("Sampler0", texture, 0);
		shaderProgram.setUniform("hardness", hardness);

		float borderStart = borderEndX - borderSize;

		Point pos0 = MatrixUtils.scale(matrix, borderStart, 0.0f);
		Point pos1 = MatrixUtils.scale(matrix, borderEndX, 0.0f);

		shaderProgram.setUniform("borderStart", pos0.getX() * scale);
		shaderProgram.setUniform("borderSize", (pos1.getX() - pos0.getX()) * scale);

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
				
				uniform float borderStart;
				uniform float borderSize;
				
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
				
				float getBorderOpacity() {
				    return 1.0 - clamp(((gl_FragCoord.x - borderStart) / borderSize), 0.0, 1.0);
				}
				
				void main() {
				    vec3 msd = texture(Sampler0, texCoord).rgb;
				    float sd = median(msd.r, msd.g, msd.b);
				
				    float screenPxDistance = screenPxRange() * (sd - 0.5);
				    float opacity = clamp(screenPxDistance + 0.5, 0.0, 1.0);
				
				    fragColor = vec4(vertexColor.rgb, vertexColor.a * opacity * getBorderOpacity());
				}
				""";
	}
}
