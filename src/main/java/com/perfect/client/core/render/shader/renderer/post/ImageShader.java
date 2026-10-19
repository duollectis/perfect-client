package com.perfect.client.core.render.shader.renderer.post;

import com.mojang.blaze3d.textures.GpuTexture;
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

public class ImageShader extends Shader {
	public ImageShader() {
		super(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);
	}

	public void draw(Framebuffer framebuffer, GpuTexture texture, Color color) {
		shaderProgram.setTexture("DiffuseSampler", framebuffer.getColorAttachment(), 0);
		shaderProgram.setTexture("ImageTexture", texture, 1);
		shaderProgram.setUniform("uResolution", mc.getFramebuffer().textureWidth, mc.getFramebuffer().textureHeight);

		shaderProgram.setUniform("uColor", ColorUtils.getArray4f(color));

		Renderer2D.preRender();
		Render.depthMask(false);

		VertexBuilder vertexBuilder = getVertexBuilder();

		vertexBuilder.pos(-1f, -1f).tex(0f, 0f);
		vertexBuilder.pos(1f, -1f).tex(1f, 0f);
		vertexBuilder.pos(1f, 1f).tex(1f, 1f);
		vertexBuilder.pos(-1f, 1f).tex(0f, 1f);

		vertexBuilder.draw(shaderProgram);

		Render.depthMask(true);
		Renderer2D.postRender();
	}

	@Override
	protected String getFragmentSource() {
		return """
				#version 330 core
				
				uniform sampler2D DiffuseSampler;
				uniform sampler2D ImageTexture;
				
				uniform vec2 uResolution;
				uniform vec4 uColor;
				
				in vec2 texCoord;
				out vec4 fragColor;
				
				void main(void) {
				    if(texture(DiffuseSampler, texCoord).a < 0.001) {
				        discard;
				        return;
				    }
				    else {
				        fragColor = texture(ImageTexture, texCoord) * uColor;
				    }
				}
				""";
	}
}
