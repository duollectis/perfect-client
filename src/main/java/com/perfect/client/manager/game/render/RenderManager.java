package com.perfect.client.manager.game.render;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.render.FramebufferUtils;
import com.perfect.client.core.render.ScreenTexture;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.render.postprocess.PostProcessTask;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.renderer.buffer.RenderBuffer;
import com.perfect.client.core.render.system.vertex.type.VertexDrawMode;
import com.perfect.client.core.render.system.vertex.type.VertexFormats;
import lombok.Getter;

public class RenderManager extends Manager {

	private final ScreenTexture postProcessTexture = new ScreenTexture();

	private boolean drewThisTick;

	@Getter
	private final RenderBuffer generalBoxOutlineBuffer = new RenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.LINES
	);

	@Getter
	private final RenderBuffer generalBoxFillBuffer = new RenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.QUADS
	);

	@Getter
	private final RenderBuffer immediateBoxOutlineBuffer = new RenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.LINES
	);

	@Getter
	private final RenderBuffer immediateBoxFillBuffer = new RenderBuffer(
			VertexFormats.POSITION_COLOR,
			VertexDrawMode.QUADS
	);

	@Override
	public void onPostRender3D() {
		if (!generalBoxOutlineBuffer.isEmpty()) {
			generalBoxOutlineBuffer.draw(BoxRenderer::drawBuilder);
		}

		if (!generalBoxFillBuffer.isEmpty()) {
			generalBoxFillBuffer.draw(BoxRenderer::drawBuilder);
		}
	}

	@Override
	public void onPostProcess() {
		if (!drewThisTick) {
			return;
		}

		Renderer2D.preRender();

		Render.enableBlend();

		Render.blendFunc(
				SrcFactor.ONE,
				DstFactor.ONE_MINUS_SRC_ALPHA
		);

		FramebufferUtils.draw(postProcessTexture.getFramebuffer(), false);

		FramebufferUtils.clear(postProcessTexture.getFramebuffer());

		Renderer2D.postRender();

		drewThisTick = false;
	}

	public void drawPostProcess(PostProcessTask postProcessTask) {
		postProcessTask.accept(postProcessTexture);
		drewThisTick = true;
	}
}
