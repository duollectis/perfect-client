package com.perfect.client.core.render;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import net.minecraft.client.gl.BufferManager;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;

import java.awt.*;

public class FramebufferUtils {

	public static void clear(Framebuffer framebuffer) {
		clear(framebuffer, 0);
	}

	public static void clear(Framebuffer framebuffer, Color color) {
		clear(framebuffer, color.getRGB());
	}

	public static void clear(Framebuffer framebuffer, int argb) {
		getEncoder().clearColorAndDepthTextures(
				framebuffer.getColorAttachment(),
				argb,
				framebuffer.getDepthAttachment(),
				1.0f
		);
	}

	private static CommandEncoder getEncoder() {
		return RenderSystem.getDevice().createCommandEncoder();
	}

	public static BufferManager getBufferManager() {
		return ((GlBackend) RenderSystem.getDevice()).getBufferManager();
	}

	public static void draw(Framebuffer framebuffer) {
		draw(framebuffer, true);
	}

	public static void draw(Framebuffer framebuffer, boolean disableBlend) {
		draw(framebuffer, 0, 0, framebuffer.textureWidth, framebuffer.textureHeight, disableBlend);
	}

	public static void draw(Framebuffer framebuffer, int x, int y, int width, int height, boolean disableBlend) {
		Render.colorMask(true, true, true, !disableBlend);
		Render.disableDepthTest();
		Render.depthMask(false);

		GlStateManager._viewport(x, y, width, height);

		if (disableBlend) {
			Render.disableBlend();
		}

		Render.setShader(ShaderPrograms.BLIT_SCREEN);
		Render.setTexture(framebuffer.getColorAttachment());

		VertexBuilder builder = new VertexBuilder(VertexFormats.BLIT_SCREEN, VertexDrawMode.QUADS);

		builder.pos(-1.0f, -1.0f).tex(0.0f, 0.0f);
		builder.pos(1.0f, -1.0f).tex(1.0f, 0.0f);
		builder.pos(1.0f, 1.0f).tex(1.0f, 1.0f);
		builder.pos(-1.0f, 1.0f).tex(0.0f, 1.0f);

		builder.draw();

		Render.depthMask(true);
		Render.colorMask(true, true, true, true);
	}

	public static void beginWrite(Framebuffer framebuffer) {
		Render.bindFramebuffer(getFbo(framebuffer));
	}

	public static int getFbo(Framebuffer framebuffer) {
		GpuTextureView colorView = framebuffer.getColorAttachmentView();
		GpuTextureView depthView = framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null;

		if (colorView == null) {
			return 0;
		}

		return ((GlTexture) colorView.texture()).getOrCreateFramebuffer(
				getBufferManager(),
				depthView == null ? null : depthView.texture()
		);
	}

	public static int getFbo(GpuTextureView textureView) {
		return ((GlTexture) textureView.texture()).getOrCreateFramebuffer(getBufferManager(), null);
	}
}
