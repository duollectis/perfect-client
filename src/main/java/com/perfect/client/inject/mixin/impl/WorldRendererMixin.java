package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IWorldRenderer;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.Handle;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin implements IWorldRenderer {

	@Shadow
	@Nullable
	public Framebuffer entityOutlineFramebuffer;

	@Shadow
	@Final
	public DefaultFramebufferSet framebufferSet;

	@Unique
	private Stack<Framebuffer> framebufferStack;

	@Unique
	private Stack<Handle<Framebuffer>> framebufferHandleStack;

	@Inject(
			method = "<init>",
			at = @At("TAIL")
	)
	private void init$IWorldRenderer(CallbackInfo info) {
		framebufferStack = new ObjectArrayList<>();
		framebufferHandleStack = new ObjectArrayList<>();
	}

	@Override
	public void perfect$pushEntityOutlineFramebuffer(Framebuffer framebuffer) {
		framebufferStack.push(entityOutlineFramebuffer);
		entityOutlineFramebuffer = framebuffer;

		framebufferHandleStack.push(framebufferSet.entityOutlineFramebuffer);
		framebufferSet.entityOutlineFramebuffer = () -> framebuffer;
	}

	@Override
	public void perfect$popEntityOutlineFramebuffer() {
		entityOutlineFramebuffer = framebufferStack.pop();
		framebufferSet.entityOutlineFramebuffer = framebufferHandleStack.pop();
	}
}
