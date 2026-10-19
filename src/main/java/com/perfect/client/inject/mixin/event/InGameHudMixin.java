package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	private void perfect$onRenderHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
		RenderUtils.set2DProjection();

		PerfectClient.EVENT_MANAGER.onRender2D(new Render2DEvent(context));

		RenderUtils.pop2DProjection();
	}
}
