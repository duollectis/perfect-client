package com.perfect.client.core.render.shader.renderer.post;

import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.system.shader.Shader;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.color.ColorUtils;
import net.minecraft.client.gl.Framebuffer;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;


public class OutlineShader extends Shader {

	public OutlineShader() {
		super(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);
	}

	public void draw(Framebuffer framebuffer, int lineWidth, Color outlineColor, Color fillColor) {
		shaderProgram.setTexture("DiffuseSampler", framebuffer.getColorAttachment(), 0);
		shaderProgram.setUniform("uResolution", mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);

		shaderProgram.setUniform("uLineWidth", lineWidth);
		shaderProgram.setUniform("uFillColor", ColorUtils.getArray4f(fillColor));
		shaderProgram.setUniform("uOutlineColor", ColorUtils.getArray4f(outlineColor));

		Renderer2D.preRender(false);
		Render.depthMask(false);

		VertexBuilder vertexBuilder = getVertexBuilder();

		vertexBuilder.pos(-1.0f, -1.0f).tex(0.0f, 0.0f);
		vertexBuilder.pos(1.0f, -1.0f).tex(1.0f, 0.0f);
		vertexBuilder.pos(1.0f, 1.0f).tex(1.0f, 1.0f);
		vertexBuilder.pos(-1.0f, 1.0f).tex(0.0f, 1.0f);

		vertexBuilder.draw(shaderProgram);

		Render.depthMask(true);
		Renderer2D.postRender();
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D DiffuseSampler;
				uniform vec2 uResolution;
				
				uniform int uLineWidth;
				uniform vec4 uFillColor;
				uniform vec4 uOutlineColor;
				
				in vec2 texCoord;
				out vec4 fragColor;
				
				void main(void) {
				    if(texture(DiffuseSampler, texCoord).a < 0.001) {
				        discard;
				        return;
				    }
				    else {
				        vec2 offset = 1. / uResolution.xy;
				
				        if (texture(DiffuseSampler, texCoord + vec2(uLineWidth, 0) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord - vec2(uLineWidth, 0) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord + vec2(0, uLineWidth) * offset).a > 0.001 &&
                            texture(DiffuseSampler, texCoord - vec2(0, uLineWidth) * offset).a > 0.001) {
	                            fragColor = uFillColor;
	                            return;
                        }
    
				        for(int x = -uLineWidth; x <= uLineWidth; x++) {
				            for(int y = -uLineWidth; y <= uLineWidth; y++) {
				                vec4 tex = texture(DiffuseSampler, texCoord + offset.xy * vec2(float(x), float(y)));
				
				                if(tex.a < 0.001) {
				                    fragColor = uOutlineColor;
				                    return;
				                }
				            }
				        }
				
				        fragColor = uFillColor;
				    }
				}
				""";
	}
}
