package com.perfect.client.inject.mixin.event;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.ScreenChangedEvent;
import com.perfect.client.manager.system.FunctionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

	@Shadow
	public ClientWorld world;

	@Shadow
	@Nullable
	public Screen currentScreen;

	@Inject(
			method = "render",
			at = @At("HEAD")
	)
	private void perfect$onRender(boolean tick, CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onStartRenderTick();
	}

	@Inject(
			method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;Z)V",
			at = @At("HEAD")
	)
	private void perfect$onDisconnect(Screen disconnectionScreen, boolean transferring, CallbackInfo ci) {
		if (world != null) {
			PerfectClient.EVENT_MANAGER.onGameLeft();
		}
	}

	@ModifyReturnValue(
			method = "reloadResources(ZLnet/minecraft/client/MinecraftClient$LoadingContext;)Ljava/util/concurrent/CompletableFuture;",
			at = @At("RETURN")
	)
	private CompletableFuture<Void> perfect$onReloadResourcesNewCompletableFuture(CompletableFuture<Void> original) {
		return original.thenRun(PerfectClient.EVENT_MANAGER::onResourcePacksReloaded);
	}

	@Inject(
			at = @At("HEAD"),
			method = "tick"
	)
	private void perfect$onPreTick(CallbackInfo info) {
		PerfectClient.EVENT_MANAGER.onPreTick();
	}

	@Inject(
			at = @At("TAIL"),
			method = "tick"
	)
	private void perfect$onPostTick(CallbackInfo info) {
		PerfectClient.EVENT_MANAGER.onPostTick();
	}

	@Inject(
			at = @At("HEAD"),
			method = "setScreen",
			cancellable = true
	)
	public void perfect$setScreen(Screen screen, CallbackInfo info) {
		ScreenChangedEvent event = new ScreenChangedEvent(screen, currentScreen);
		PerfectClient.EVENT_MANAGER.onScreenChanged(event);

		if (event.isCancelled()) {
			info.cancel();
		}
	}

	@Inject(
			at = @At("TAIL"),
			method = "setScreen"
	)
	public void perfect$setScreenPost(Screen screen, CallbackInfo info) {
		ScreenChangedEvent event = new ScreenChangedEvent(screen, currentScreen);

		PerfectClient.EVENT_MANAGER.onPostScreenChanged(event);
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"
			)
	)
	private void perfect$handleInputEvents(CallbackInfo ci) {
		PerfectClient.EVENT_MANAGER.onHandleInput();
	}

	@Redirect(
			method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/option/KeyBinding;unpressAll()V"
			)
	)
	private void perfect$redirectUnpressAll() {
		if (!FunctionManager.guiMove.shouldWork()) {
			KeyBinding.unpressAll();
		}
	}
}
