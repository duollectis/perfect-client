package com.perfect.client.core.render.texture;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.perfect.client.PerfectClient;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL40.*;

public class TextureLoader implements AutoCloseable {

	private final Map<String, LoadedTexture> loadedTextures = new HashMap<>();

	public LoadedTexture loadTexture(String path) {
		return loadTexture(PerfectClient.getResource(path), path);
	}

	
	public LoadedTexture loadTexture(InputStream inputStream, String id) {
		if (loadedTextures.containsKey(id)) {
			return loadedTextures.get(id);
		}

		BufferedImage image;
		try {
			image = ImageIO.read(inputStream);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to load texture: %s".formatted(id), e);
		}

		int[] pixels = new int[image.getWidth() * image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = pixels[y * image.getWidth() + x];
				buffer.put((byte) ((pixel >> 16) & 0xFF)); // R
				buffer.put((byte) ((pixel >> 8) & 0xFF));  // G
				buffer.put((byte) (pixel & 0xFF));         // B
				buffer.put((byte) ((pixel >> 24) & 0xFF)); // A
			}
		}
		buffer.flip();

		int textureID = GlStateManager._genTexture();
		GlStateManager._bindTexture(textureID);

		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexImage2D(
				GL_TEXTURE_2D,
				0,
				GL_RGBA,
				image.getWidth(),
				image.getHeight(),
				0,
				GL_RGBA,
				GL_UNSIGNED_BYTE,
				buffer
		);

		glGenerateMipmap(GL_TEXTURE_2D);

		GlStateManager._bindTexture(0);

		LoadedTexture texture = new LoadedTexture(textureID);
		loadedTextures.put(id, texture);

		return texture;
	}

	@Override
	public void close() throws Exception {
		IntBuffer buffer = BufferUtils.createIntBuffer(loadedTextures.size());
		for (LoadedTexture textureID : loadedTextures.values()) {
			buffer.put(textureID.getGlId());
		}
		buffer.flip();
		GL40.glDeleteTextures(buffer);
		loadedTextures.clear();
	}
}