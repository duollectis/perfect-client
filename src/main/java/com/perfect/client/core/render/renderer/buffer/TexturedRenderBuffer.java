package com.perfect.client.core.render.renderer.buffer;

import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormat;
import lombok.Getter;
import net.minecraft.client.gl.Framebuffer;

import java.util.function.Consumer;

@Getter
public class TexturedRenderBuffer extends RenderBuffer {

	private final ScreenTexture texture = new ScreenTexture();

	public TexturedRenderBuffer(VertexFormat format, VertexDrawMode drawMode) {
		super(format, drawMode);
	}

	@Override
	public void draw(Consumer<VertexBuilder> drawAction, boolean clear) {
		texture.write(() -> super.draw(drawAction, clear), clear);
	}

	public Framebuffer getFramebuffer() {
		return texture.getFramebuffer();
	}
}
