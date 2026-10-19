package com.perfect.client.inject.mixinInterface;

import net.minecraft.client.gl.Framebuffer;

public interface IWorldRenderer {

	void perfect$pushEntityOutlineFramebuffer(Framebuffer framebuffer);
	void perfect$popEntityOutlineFramebuffer();
}
