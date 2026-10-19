package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.input.keyboard.CharEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.utils.input.KeyAction;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(Keyboard.class)
public class KeyboardMixin {

	@Inject(
			method = "onKey",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
		if (key == GLFW.GLFW_KEY_UNKNOWN) {
			return;
		}

		KeyAction keyAction = KeyAction.get(action);

		KeyEvent globalKeyEvent = new KeyEvent(key, modifiers, keyAction);
		PerfectClient.EVENT_MANAGER.onGlobalKey(globalKeyEvent);

		if (globalKeyEvent.isCancelled()) {
			ci.cancel();
		}
		else if (window == mc.getWindow().getHandle()) {
			KeyEvent keyEvent = new KeyEvent(key, modifiers, keyAction);
			PerfectClient.EVENT_MANAGER.onKey(keyEvent);

			if (keyEvent.isCancelled()) {
				ci.cancel();
			}
		}
	}

	@Inject(
			method = "onChar",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onChar(long window, int codePoint, int modifiers, CallbackInfo ci) {
		if (window == mc.getWindow().getHandle()) {
			CharEvent event = new CharEvent((char) codePoint, modifiers);
			PerfectClient.EVENT_MANAGER.onChar(event);

			if (event.isCancelled()) {
				ci.cancel();
			}
		}
	}
}
