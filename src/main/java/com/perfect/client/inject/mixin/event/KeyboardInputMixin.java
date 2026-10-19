package com.perfect.client.inject.mixin.event;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin extends Input {

	@Inject(
			method = "tick",
			at = @At(value = "TAIL")
	)
	private void perfect$onSneak(CallbackInfo ci) {
		MovementInputEvent event = new MovementInputEvent();
		PerfectClient.EVENT_MANAGER.onMovementInput(event);

		boolean forward = event.forward.getOrDefault(playerInput.forward());
		boolean back = event.back.getOrDefault(playerInput.backward());
		boolean right = event.right.getOrDefault(playerInput.right());
		boolean left = event.left.getOrDefault(playerInput.left());

		boolean sneaking = event.sneaking.getOrDefault(playerInput.sneak());
		boolean jumping = event.jumping.getOrDefault(playerInput.jump());
		boolean sprinting = event.sprinting.getOrDefault(playerInput.sprint());

		playerInput = new PlayerInput(forward, back, left, right, jumping, sneaking, sprinting);

		if (event.forward.isSet() || event.back.isSet() || event.right.isSet() || event.left.isSet()) {
			float f = KeyboardInput.getMovementMultiplier(playerInput.forward(), playerInput.backward());
			float g = KeyboardInput.getMovementMultiplier(playerInput.left(), playerInput.right());
			movementVector = new Vec2f(g, f).normalize();
		}
	}
}
