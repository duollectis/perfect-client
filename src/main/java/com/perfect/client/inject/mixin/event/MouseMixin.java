package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.event.events.input.mouse.MouseScrollEvent;
import com.perfect.client.core.utils.input.KeyAction;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(Mouse.class)
public class MouseMixin {

	@Inject(
			method = "onMouseButton",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
		KeyAction keyAction = KeyAction.get(action);

		MouseButtonEvent globalMouseButtonEvent = new MouseButtonEvent(button, mods, keyAction);
		PerfectClient.EVENT_MANAGER.onGlobalMouseButton(globalMouseButtonEvent);

		if (globalMouseButtonEvent.isCancelled()) {
			ci.cancel();
		}
		else if (window == mc.getWindow().getHandle()) {
			MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(button, mods, keyAction);
			PerfectClient.EVENT_MANAGER.onMouseButton(mouseButtonEvent);

			if (mouseButtonEvent.isCancelled()) {
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "onMouseScroll",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		MouseScrollEvent event = new MouseScrollEvent(vertical);
		PerfectClient.EVENT_MANAGER.onMouseScroll(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "onCursorPos",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onMouseMove(long window, double x, double y, CallbackInfo ci) {
		double scale = mc.getWindow().getScaleFactor();

		MouseMoveEvent event = new MouseMoveEvent((float) (x / scale), (float) (y / scale));
		PerfectClient.EVENT_MANAGER.onMouseMove(event);

		if (event.isCancelled()) {
			ci.cancel();
		}
	}
}
