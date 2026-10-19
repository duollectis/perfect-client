package com.perfect.client.inject.mixin.impl;

import com.perfect.client.Entry;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.manager.Managers;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

	@Inject(
			method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"
			)
	)
	public void perfect$renderWatermark(
			DrawContext context,
			int mouseX,
			int mouseY,
			float deltaTicks,
			CallbackInfo ci
	) {
		int nameWidth = mc.textRenderer.getWidth(PerfectClient.NAME + " ");

		context.drawTextWithShadow(
				mc.textRenderer,
				PerfectClient.NAME,
				2,
				2,
				Managers.COLOR.get(Managers.COLOR.MAIN).getRGB()
		);
		context.drawTextWithShadow(mc.textRenderer, Entry.DISPLAY_VERSION, 2 + nameWidth, 2, -1);
	}
}
