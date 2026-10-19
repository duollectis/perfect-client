package com.perfect.client.inject.mixin.module.noRender;

import com.perfect.client.functional.function.visuals.noRender.Elements;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static com.perfect.client.manager.system.FunctionManager.noRender;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Inject(
			method = "renderStatusEffectOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderStatusEffectOverlay(CallbackInfo ci) {
		if (noRender.contains(Elements.POTION_ICONS)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderPortalOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderPortalOverlay(DrawContext context, float nauseaStrength, CallbackInfo ci) {
		if (noRender.contains(Elements.PORTAL_OVERLAY)) {
			ci.cancel();
		}
	}

	@ModifyArgs(
			method = "renderMiscOverlays",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 0
			)
	)
	private void perfect$onRenderPumpkinOverlay(Args args) {
		if (noRender.contains(Elements.PUMPKIN_OVERLAY)) {
			args.set(2, 0.0f);
		}
	}

	@ModifyArgs(
			method = "renderMiscOverlays",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/InGameHud;renderOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;F)V",
					ordinal = 1
			)
	)
	private void perfect$onRenderPowderedSnowOverlay(Args args) {
		if (noRender.contains(Elements.LIQUID_OVERLAY)) {
			args.set(2, 0.0f);
		}
	}

	@Inject(
			method = "renderVignetteOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
		if (noRender.contains(Elements.VIGNETTE)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderScoreboardSidebar(
			DrawContext context,
			ScoreboardObjective objective,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.SCOREBOARD)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderScoreboardSidebar(
			DrawContext context,
			RenderTickCounter tickCounter,
			CallbackInfo ci
	) {
		if (noRender.contains(Elements.SCOREBOARD)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderNauseaOverlay",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onRenderNausea(DrawContext context, float nauseaStrength, CallbackInfo ci) {
		if (noRender.contains(Elements.NAUSEA)) {
			ci.cancel();
		}
	}
}
