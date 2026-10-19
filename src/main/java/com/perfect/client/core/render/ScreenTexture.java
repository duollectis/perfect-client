package com.perfect.client.core.render;

import com.mojang.blaze3d.textures.GpuTexture;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import net.minecraft.client.gl.Framebuffer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class ScreenTexture {

	private Framebuffer framebuffer;
	private int oldWrapS;
	private int oldWrapT;

	public void write(Runnable drawAction) {
		write(drawAction, true);
	}

	public void write(Runnable drawAction, boolean clear) {
		beginWrite(clear);

		drawAction.run();

		Render.bindFramebuffer(0);
		endWrite();
	}

	private void beginWrite(boolean clear) {
		framebuffer = Renderer2D.initBuffer(framebuffer);

		if (clear) {
			clear();
		}

		FramebufferUtils.beginWrite(framebuffer);
		storageAndApplyParamsValues();

		Render.setActiveFramebuffer(framebuffer);
	}

	private void storageAndApplyParamsValues() {
		oldWrapS = GL11.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S);
		oldWrapT = GL11.glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T);

		// Вот эта хуйня в идеале должна быть, потому что она обрезает шейдеры за границами экрана
		Render.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		Render.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	}

	private void endWrite() {
		resetParams();

		Render.setActiveFramebuffer(null);
	}

	private void resetParams() {
		Render.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, oldWrapS);
		Render.texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, oldWrapT);
	}

	public void clear() {
		framebuffer = Renderer2D.initBuffer(framebuffer);
		FramebufferUtils.clear(framebuffer);
	}

	public GpuTexture getColorTexture() {
		framebuffer = Renderer2D.initBuffer(framebuffer);
		return framebuffer.getColorAttachment();
	}

	public Framebuffer getFramebuffer() {
		framebuffer = Renderer2D.initBuffer(framebuffer);
		return framebuffer;
	}
}
