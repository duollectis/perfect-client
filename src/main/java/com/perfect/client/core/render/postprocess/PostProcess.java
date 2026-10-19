package com.perfect.client.core.render.postprocess;

import com.perfect.client.core.render.FramebufferUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.entity.Entity;

import static com.perfect.client.PerfectClient.mc;

@Getter
@Setter
public abstract class PostProcess {

	protected OutlineVertexConsumerProvider vertexConsumerProvider;
	protected Framebuffer framebuffer;

	public PostProcess() {
		vertexConsumerProvider = createVertexConsumerProvider();
		framebuffer = createFramebuffer();
	}

	public abstract boolean skipDraw();

	public boolean shouldDraw(Entity entity) {
		return true;
	}

	protected void preDraw() {}

	protected void postDraw() {}

	public void beginWrite() {
		if (skipDraw()) {
			return;
		}

		FramebufferUtils.clear(framebuffer);
		FramebufferUtils.beginWrite(mc.getFramebuffer());
	}

	public void endWrite(Runnable draw) {
		if (skipDraw()) {
			return;
		}

		preDraw();
		draw.run();
		postDraw();
	}

	public void draw() {
		if (skipDraw()) {
			return;
		}

		FramebufferUtils.beginWrite(mc.getFramebuffer());
		draw(framebuffer);
	}

	protected abstract void draw(Framebuffer framebuffer);

	public void onResized(int width, int height) {
		if (framebuffer == null) {
			return;
		}
		framebuffer.resize(width, height);
	}

	private static OutlineVertexConsumerProvider createVertexConsumerProvider() {
		return new OutlineVertexConsumerProvider(mc.getBufferBuilders().getEntityVertexConsumers());
	}

	private static Framebuffer createFramebuffer() {
		int width = mc.getWindow().getFramebufferWidth();
		int height = mc.getWindow().getFramebufferHeight();

		return new SimpleFramebuffer("perfect&framebuffer", width, height, true);
	}
}
