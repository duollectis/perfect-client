package com.perfect.client.inject.mixin.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.chat.MessageSendEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.ChatUtils;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.perfect.client.PerfectClient.mc;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

	@Unique
	private boolean ignoreChatMessage;

	@Shadow
	public abstract void sendChatMessage(String content);

	@Inject(
			method = "sendChatMessage",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onSendChatMessage(String message, CallbackInfo ci) {
		if (ignoreChatMessage)
			return;

		if (message.startsWith(PerfectClient.prefix)) {
			try {
				Managers.COMMAND.dispatch(message.substring(PerfectClient.prefix.length()));
			}
			catch (CommandSyntaxException e) {
				ChatUtils.error(0, e.getMessage());
			}

			mc.inGameHud.getChatHud().addToMessageHistory(message);
			ci.cancel();
			return;
		}

		MessageSendEvent event = new MessageSendEvent(message);
		PerfectClient.EVENT_MANAGER.onMessageSend(event);

		if (!event.isCancelled()) {
			ignoreChatMessage = true;
			sendChatMessage(event.getMessage());
			ignoreChatMessage = false;
		}

		ci.cancel();
	}
}
