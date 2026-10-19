package com.perfect.client.inject.mixin.impl;

import com.mojang.authlib.GameProfile;
import com.perfect.client.inject.mixinInterface.IMessageHandler;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;

@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin implements IMessageHandler {

	@Unique
	private GameProfile sender;

	@Inject(
			method = "processChatMessageInternal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
					shift = At.Shift.BEFORE
			)
	)
	private void perfect$beforeAddMessage(
			MessageType.Parameters params,
			SignedMessage message,
			Text decorated,
			GameProfile sender,
			boolean onlyShowSecureChat,
			Instant receptionTimestamp,
			CallbackInfoReturnable<Boolean> cir
	) {
		this.sender = sender;
	}

	@Inject(
			method = "processChatMessageInternal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
					shift = At.Shift.AFTER
			)
	)
	private void perfect$afterAddMessage(
			MessageType.Parameters params,
			SignedMessage message,
			Text decorated,
			GameProfile sender,
			boolean onlyShowSecureChat,
			Instant receptionTimestamp,
			CallbackInfoReturnable<Boolean> info
	) {
		this.sender = null;
	}

	@Override
	public GameProfile perfect$getSender() {
		return sender;
	}
}