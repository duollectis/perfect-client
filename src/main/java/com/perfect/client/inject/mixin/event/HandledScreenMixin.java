package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.RenderHandledScreenEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T> {

	@Shadow
	public int x;
	@Shadow
	public int y;

	protected HandledScreenMixin(Text title) {
		super(title);
	}

	@Shadow
	@Final
	protected T handler;

	@Inject(
			method = "render",
			at = @At("TAIL")
	)
	private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		RenderHandledScreenEvent event = new RenderHandledScreenEvent(context, this.handler, this.x, this.y);

		PerfectClient.EVENT_MANAGER.onRenderSlot(event);
	}
}