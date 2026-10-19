package com.perfect.client.core.render.shader.renderer.post;

import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import net.minecraft.client.gl.Framebuffer;

import static com.perfect.client.PerfectClient.mc;


public class GlowShader extends Shader {

	private final ScreenTexture screenTexture = new ScreenTexture();

	public GlowShader() {
		super(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);
	}

	public void draw(Framebuffer input, ScreenTexture output, float multiplier, float strength) {
		screenTexture.write(() -> FramebufferUtils.draw(input));

		shaderProgram.setUniform(
				"uResolution",
				(float) input.textureWidth,
				(float) input.textureHeight
		);

		shaderProgram.setUniform("uStrength", strength);
		shaderProgram.setUniform("uMultiplier", multiplier);

		VertexBuilder builder = getVertexBuilder();

		builder.pos(-1f, -1f).tex(0f, 0f);
		builder.pos(1f, -1f).tex(1f, 0f);
		builder.pos(1f, 1f).tex(1f, 1f);
		builder.pos(-1f, 1f).tex(0f, 1f);

		//Process
		Renderer2D.preRender();
		Render.depthMask(false);

		shaderProgram.setUniform("uVertical", 0);
		shaderProgram.setTexture("OriginalSampler", input.getColorAttachment(), 0);
		shaderProgram.setTexture("DiffuseSampler", input.getColorAttachment(), 1);

		screenTexture.write(() -> builder.draw(shaderProgram));

		shaderProgram.setUniform("uVertical", 1);
		shaderProgram.setTexture("OriginalSampler", input.getColorAttachment(), 0);
		shaderProgram.setTexture("DiffuseSampler", screenTexture.getColorTexture(), 1);

		output.write(() -> builder.draw(shaderProgram));

		Renderer2D.postRender();
		Render.depthMask(true);

		FramebufferUtils.beginWrite(mc.getFramebuffer());
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D OriginalSampler;
				uniform sampler2D DiffuseSampler;
				
				uniform vec2 uResolution;
				
				uniform float uStrength;
				uniform float uMultiplier;
				uniform int uVertical;
				
				in vec2 texCoord;
				out vec4 fragColor;
				
				const float kernel[21] = float[](0.041555, 0.041348, 0.040733, 0.039728, 0.038363, 0.036676, 0.034715, 0.032532, 0.030183, 0.027726, 0.025215, 0.022704, 0.020239, 0.017863, 0.015609, 0.013504, 0.011566, 0.009808, 0.008235, 0.006845, 0.005633);
				
				vec4 addOffset(vec4 current, vec4 offsetTexture, float kern) {
				    if(offsetTexture.a > 0) return offsetTexture * kern;
				    else return current * kern;
				}
				
				vec4 horizontal(void) {
				    float offset = uStrength / uResolution.x;
				
				    vec4 current = texture(DiffuseSampler, texCoord) * kernel[0];
				    vec4 blur = current;
				
				    for(int i = 1; i < 21; ++i) {
				        blur += texture(DiffuseSampler, texCoord - vec2(float(i) * offset, 0.0)) * kernel[i];
				        blur += texture(DiffuseSampler, texCoord + vec2(float(i) * offset, 0.0)) * kernel[i];
				    }
				
				    return blur;
				}
				
				vec4 vertical(void) {
				    float offset = uStrength / uResolution.y;
				
				    vec4 current = texture(DiffuseSampler, texCoord) * kernel[0];
				    vec4 blur = current;
				
				    for(int i = 1; i < 21; ++i) {
				        blur += texture(DiffuseSampler, texCoord - vec2(0.0, float(i) * offset)) * kernel[i];
				        blur += texture(DiffuseSampler, texCoord + vec2(0.0, float(i) * offset)) * kernel[i];
				    }
				
				    return blur;
				}
				
				void main(void) {
				    vec4 blur;
				
				    if(uVertical == 1) {
				        blur = vertical();
				
				        if(texture(OriginalSampler, texCoord).a > 0.001) {
				            blur.a = (1.0) - blur.a;
				        }
				
				        fragColor = vec4(blur.rgb, blur.a * uMultiplier);
				    } else {
				        fragColor = horizontal();
				    }
				}
				""";
	}
}
