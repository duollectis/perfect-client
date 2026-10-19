package com.perfect.client.core.render.shader.renderer;

import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import net.minecraft.client.gl.Framebuffer;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.mc;

public class BlurShader extends Shader {

	private final ScreenTexture screenTexture = new ScreenTexture();

	public BlurShader() {
		super(VertexFormats.POSITION_TEXTURE, VertexDrawMode.QUADS);
	}

	public void draw(
			Matrix3x2fStack matrices,
			ScreenTexture output,
			float x,
			float y,
			float width,
			float height,
			float[] strength
	) {
		draw(matrices, mc.getFramebuffer(), output, x, y, width, height, strength, false);
	}

	private void draw(
			Matrix3x2fStack matrices,
			Framebuffer input,
			ScreenTexture output,
			float x,
			float y,
			float width,
			float height,
			float[] strength,
			boolean blending
	) {
		screenTexture.write(() -> FramebufferUtils.draw(input, !blending));

		shaderProgram.setUniform(
				"uResolution",
				(float) input.textureWidth,
				(float) input.textureHeight
		);

		float x1 = x + width;
		float y1 = y + height;

		VertexBuilder vertexBuilder = getVertexBuilder();

		vertexBuilder.pos(matrices, x, y).screenTex(matrices, x, y);
		vertexBuilder.pos(matrices, x1, y).screenTex(matrices, x1, y);
		vertexBuilder.pos(matrices, x1, y1).screenTex(matrices, x1, y1);
		vertexBuilder.pos(matrices, x, y1).screenTex(matrices, x, y1);

		//Process
		Renderer2D.preRender();

		shaderProgram.setUniform("uStrength", strength[0]);
		shaderProgram.setUniform("uVertical", 0);

		shaderProgram.setTexture("DiffuseSampler", input.getColorAttachment(), 0);

		screenTexture.write(() -> vertexBuilder.draw(shaderProgram));

		shaderProgram.setUniform("uStrength", strength[1]);
		shaderProgram.setUniform("uVertical", 1);

		shaderProgram.setTexture("DiffuseSampler", screenTexture.getColorTexture(), 0);

		output.write(() -> vertexBuilder.draw(shaderProgram));

		Renderer2D.postRender();

		FramebufferUtils.beginWrite(mc.getFramebuffer());
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D DiffuseSampler;
				uniform vec2 uResolution;
				
				uniform float uStrength;
				uniform int uVertical;
				
				in vec2 texCoord;
				out vec4 fragColor;
				
				const float kernel[21] = float[](0.041555, 0.041348, 0.040733, 0.039728, 0.038363, 0.036676, 0.034715, 0.032532, 0.030183, 0.027726, 0.025215, 0.022704, 0.020239, 0.017863, 0.015609, 0.013504, 0.011566, 0.009808, 0.008235, 0.006845, 0.005633);
				
				vec3 horizontal(void) {
				    float offset = uStrength / uResolution.x;
				
				    vec3 blur = texture(DiffuseSampler, texCoord).rgb * kernel[0];
				    for(int i = 1; i < 21; ++i) {
				        blur += texture(DiffuseSampler, texCoord - vec2(float(i) * offset, 0.0)).rgb * kernel[i];
				        blur += texture(DiffuseSampler, texCoord + vec2(float(i) * offset, 0.0)).rgb * kernel[i];
				    }
				
				    return blur;
				}
				
				vec3 vertical(void) {
				    float offset = uStrength / uResolution.y;
				
				    vec3 blur = texture(DiffuseSampler, texCoord).rgb * kernel[0];
				    for(int i = 1; i < 21; ++i) {
				        blur += texture(DiffuseSampler, texCoord - vec2(0.0, float(i) * offset)).rgb * kernel[i];
				        blur += texture(DiffuseSampler, texCoord + vec2(0.0, float(i) * offset)).rgb * kernel[i];
				    }
				
				    return blur;
				}
				
				void main(void) {
				    vec3 blur;
				    if(uVertical == 1) {
				        blur = vertical();
				    } else {
				        blur = horizontal();
				    }
				
				    fragColor = vec4(blur, 1.0);
				}
				""";
	}
}

