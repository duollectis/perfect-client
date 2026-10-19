package com.perfect.client.functional.function.visuals.imageFilter.utils;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.shader.ShaderPrograms;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import com.perfect.client.core.utils.RenderUtils;
import lombok.Getter;
import lombok.Setter;

import static com.perfect.client.manager.system.FunctionManager.imageFilter;

public class CustomLightMap {

	@Getter
	private static GpuTextureView textureView;

	@Getter
	@Setter
	private static boolean rendering = false;

	public static void create() {
		GpuDevice gpuDevice = RenderSystem.getDevice();

		GpuTexture texture = gpuDevice.createTexture("Perfect Light Texture", 12, TextureFormat.RGBA8, 16, 16, 1, 1);

		texture.setTextureFilter(FilterMode.LINEAR, false);
		textureView = gpuDevice.createTextureView(texture);

		gpuDevice.createCommandEncoder().clearColorTexture(texture, -1);
	}

	public static void enable() {
		RenderSystem.setShaderTexture(2, textureView);
	}

	public static void update() {
		Render.colorMask(true, true, true, false);
		Render.disableDepthTest();
		Render.depthMask(false);
		Render.disableBlend();

		int fbo = FramebufferUtils.getFbo(textureView);
		Render.bindFramebuffer(fbo);

		Render.setColor(imageFilter.getColor());
		Render.setShader(ShaderPrograms.BLIT_SCREEN_LIGHT_MAP);

		VertexBuilder builder = new VertexBuilder(VertexFormats.BLIT_SCREEN_LIGHT_MAP, VertexDrawMode.QUADS);

		builder.pos(-1f, -1f);
		builder.pos(1f, -1f);
		builder.pos(1f, 1f);
		builder.pos(-1f, 1f);

		builder.draw();

		Render.depthMask(true);
		Render.colorMask(true, true, true, true);

		RenderUtils.postRender();
	}
}
