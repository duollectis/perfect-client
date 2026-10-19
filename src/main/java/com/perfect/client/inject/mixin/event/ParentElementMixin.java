package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.system.Cancellable;
import net.minecraft.client.gui.ParentElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ParentElement.class)
public interface ParentElementMixin {

	@Inject(
			method = "mouseClicked",
			at = @At("HEAD"),
			cancellable = true
	)
	default void perfect$mouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
		returnIfCancelled(cir, PerfectClient.EVENT_MANAGER::onScreenMouse);
	}

	@Inject(
			method = "mouseDragged",
			at = @At("HEAD"),
			cancellable = true
	)
	default void perfect$mouseDragged(
			double mouseX,
			double mouseY,
			int button,
			double deltaX,
			double deltaY,
			CallbackInfoReturnable<Boolean> cir
	) {
		returnIfCancelled(cir, PerfectClient.EVENT_MANAGER::onScreenMouse);
	}

	@Inject(
			method = "mouseScrolled",
			at = @At("HEAD"),
			cancellable = true
	)
	default void perfect$mouseScrolled(
			double mouseX,
			double mouseY,
			double horizontalAmount,
			double verticalAmount,
			CallbackInfoReturnable<Boolean> cir
	) {
		returnIfCancelled(cir, PerfectClient.EVENT_MANAGER::onScreenMouse);
	}

	@Inject(
			method = "keyPressed",
			at = @At("HEAD"),
			cancellable = true
	)
	default void perfect$onKey(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
		returnIfCancelled(cir, PerfectClient.EVENT_MANAGER::onScreenKey);
	}

	@Unique
	default void returnIfCancelled(CallbackInfoReturnable<Boolean> cir, Consumer<Cancellable> eventConsumer) {
		Cancellable event = new Cancellable();

		eventConsumer.accept(event);

		if (event.isCancelled()) {
			cir.setReturnValue(true);
		}
	}
}
