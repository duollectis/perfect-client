package com.perfect.client.core.render.renderer;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.system.shader.ShaderProgram;
import com.perfect.client.core.render.texture.LoadedTexture;
import com.perfect.client.core.utils.color.ColorUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlCommandEncoder;
import net.minecraft.client.texture.GlTexture;

import java.awt.*;

import static org.lwjgl.opengl.GL40.*;


public class Render {

	@Getter
	@Setter
	private static int textureId = 0;

	@Getter
	private static final float[] color = new float[]{1f, 1f, 1f, 1f};

	@Getter
	@Setter
	private static ShaderProgram shader;

	@Getter
	@Setter
	private static Framebuffer activeFramebuffer;

	public static void setTexture(LoadedTexture loadedTexture) {
		textureId = loadedTexture.getGlId();
	}

	public static void setTexture(GpuTexture gpuTexture) {
		textureId = ((GlTexture) gpuTexture).getGlId();
	}

	public static void setColor(Color color) {
		setColor(ColorUtils.getArray4f(color));
	}

	public static void setColor(float[] color) {
		setColor(color[0], color[1], color[2], color[3]);
	}

	public static void setColor(float r, float g, float b, float a) {
		color[0] = r;
		color[1] = g;
		color[2] = b;
		color[3] = a;
	}

	public static void enableBlend() {
		GlStateManager._enableBlend();
	}

	public static void disableBlend() {
		GlStateManager._disableBlend();
	}

	public static void blendFunc(SrcFactor srcFactor, DstFactor dstFactor) {
		GlStateManager._blendFuncSeparate(srcFactor.gl, dstFactor.gl, srcFactor.gl, dstFactor.gl);
	}

	public static void blendFuncSeparate(
			SrcFactor srcFactor,
			DstFactor dstFactor,
			SrcFactor srcAlpha,
			DstFactor dstAlpha
	) {
		GlStateManager._blendFuncSeparate(srcFactor.gl, dstFactor.gl, srcAlpha.gl, dstAlpha.gl);
	}

	public static void defaultBlendFunc() {
		GlStateManager._blendFuncSeparate(
				SrcFactor.SRC_ALPHA.gl,
				DstFactor.ONE_MINUS_SRC_ALPHA.gl,
				SrcFactor.ONE.gl,
				DstFactor.ZERO.gl
		);
	}


	public static void resetActiveFramebuffer() {
		activeFramebuffer = null;
	}

	public static void colorMask(boolean r, boolean g, boolean b, boolean a) {
		GlStateManager._colorMask(r, g, b, a);
	}

	public static void enableDepthTest() {
		GlStateManager._enableDepthTest();
	}

	public static void disableDepthTest() {
		GlStateManager._disableDepthTest();
	}

	public static void enableCull() {
		GlStateManager._enableCull();
	}

	public static void disableCull() {
		GlStateManager._disableCull();
	}

	public static void enableScissor(int x, int y, int width, int height) {
		GlStateManager._enableScissorTest();
		GlStateManager._scissorBox(x, y, width, height);
	}

	public static void disableScissor() {
		GlStateManager._disableScissorTest();
	}

	public static void depthMask(boolean mask) {
		GlStateManager._depthMask(mask);
	}

	public static void activeTexture(int texture) {
		GlStateManager._activeTexture(texture);
	}

	public static void bindTexture(int texture) {
		GlStateManager._bindTexture(texture);
	}

	public static void bindFramebuffer(int fbo) {
		GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, fbo);
	}

	public static void enableLineSmooth() {
		glEnable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
	}

	public static void disableLineSmooth() {
		glDisable(GL_LINE_SMOOTH);
		glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
	}

	public static void enablePolygonSmooth() {
		glEnable(GL_POLYGON_SMOOTH);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
	}

	public static void disablePolygonSmooth() {
		glDisable(GL_POLYGON_SMOOTH);
		glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
	}

	public static void texParameter(int target, int name, int param) {
		GlStateManager._texParameter(target, name, param);
	}

	public static void invalidateProgram() {
		if (RenderSystem.getDevice().createCommandEncoder() instanceof GlCommandEncoder encoder) {
			encoder.currentProgram = null;
		}
	}
}
