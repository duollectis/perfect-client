package com.perfect.client.core.render.postprocess.processors;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.IEventListener;
import com.perfect.client.core.render.postprocess.PostProcessShaders;
import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.manager.core.ConfigManager;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.entity.Entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static com.perfect.client.manager.system.FunctionManager.chams;

public class ChamsProcessor extends EntityProcessor implements IEventListener {

	private static GpuTexture IMAGE_TEX;

	public ChamsProcessor() {
		load();
		PerfectClient.EVENT_MANAGER.subscribe(this);
	}

	public static void load() {
		try {
			File imagePath = ConfigManager.IMAGE_CHAMS;

			if (!imagePath.exists()) {
				LoggerUtils.warning("Chams texture not found at: ", imagePath);
				return;
			}

			try (InputStream inputStream = new FileInputStream(imagePath)) {
				NativeImage image = NativeImage.read(inputStream);

				if (IMAGE_TEX != null) {
					IMAGE_TEX.close();
				}

				IMAGE_TEX = RenderSystem.getDevice().createTexture(
						"chams_custom_texture",
						15,
						TextureFormat.RGBA8,
						image.getWidth(),
						image.getHeight(),
						1,
						1
				);

				RenderSystem.getDevice().createCommandEncoder().writeToTexture(IMAGE_TEX, image);

				image.close();
			}
		}
		catch (Exception e) {
			LoggerUtils.error("Error loading the chams texture: ", e);
		}
	}

	@Override
	public void onResourcePacksReloaded() {
		load();
	}

	@Override
	public boolean skipDraw() {
		return !chams.isShaderMode();
	}

	@Override
	public boolean shouldDraw(Entity entity) {
		if (skipDraw()) {
			return false;
		}

		return chams.shouldDraw(entity);
	}

	@Override
	protected void draw(Framebuffer framebuffer) {
		if (IMAGE_TEX == null) {
			return;
		}

		PostProcessShaders.IMAGE_SHADER.draw(
				framebuffer,
				IMAGE_TEX,
				chams.getShaderColor()
		);
	}
}
