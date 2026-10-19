package com.perfect.client.core.render.postprocess.processors;

import com.perfect.client.core.render.postprocess.PostProcess;
import com.perfect.client.inject.mixinInterface.IWorldRenderer;

import static com.perfect.client.PerfectClient.mc;

public abstract class EntityProcessor extends PostProcess {

	@Override
	protected void preDraw() {
		((IWorldRenderer) mc.worldRenderer).perfect$pushEntityOutlineFramebuffer(framebuffer);
	}

	@Override
	protected void postDraw() {
		((IWorldRenderer) mc.worldRenderer).perfect$popEntityOutlineFramebuffer();
	}

	public void endWrite() {
		endWrite(() -> vertexConsumerProvider.draw());
	}
}
