package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {

	@Inject(
			method = "render",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRender(CallbackInfo info) {
		if (noRender.contains(Elements.BOSS_BAR)) {
			info.cancel();
		}
	}
}
