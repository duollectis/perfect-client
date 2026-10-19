package com.perfect.client.inject.mixin.module.betterChat;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.manager.system.FunctionManager.betterChat;

@Mixin(value = ChatScreen.class, priority = 1001)
public abstract class ChatScreenMixin {

	@Shadow
	protected TextFieldWidget chatField;

	@Inject(
			method = "init",
			at = @At(value = "RETURN")
	)
	private void perfect$onInit(CallbackInfo info) {
		if (betterChat.getExtraMessageLength()) {
			chatField.setMaxLength(Integer.MAX_VALUE);
		}
	}
}