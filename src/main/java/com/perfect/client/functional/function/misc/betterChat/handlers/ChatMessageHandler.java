package com.perfect.client.functional.function.misc.betterChat.handlers;

import com.perfect.client.core.event.events.chat.MessageReceiveEvent;
import com.perfect.client.core.event.events.chat.MessageSendEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.functional.function.misc.betterChat.BetterChat;
import com.perfect.client.functional.function.misc.betterChat.BetterChatConfig;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessageHandler extends FunctionHandler<BetterChat, BetterChatConfig> {

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");

	public ChatMessageHandler(BetterChat function, BetterChatConfig config) {
		super(function, config);
	}

	@Override
	public void onMessageReceive(MessageReceiveEvent event) {
		Text message = event.getMessage();

		if (config.getTimeStamps().get()) {
			Text firstBracket = Text.literal("<").formatted(Formatting.GRAY);
			Text timeStamp = Text.literal(dateFormat.format(new Date())).formatted(Formatting.WHITE);
			Text secondBracket = Text.literal("> ").formatted(Formatting.GRAY);

			message = Text.empty().append(firstBracket).append(timeStamp).append(secondBracket).append(message);
		}

		event.setMessage(message);
	}

	@Override
	public void onMessageSend(MessageSendEvent event) {
		String message = event.getMessage();

		message = getPrefix() + message + getSuffix();

		event.setMessage(message);
	}

	private String getPrefix() {
		return config.getPrefix().isEnabled() ? config.getPrefix().get() : "";
	}

	private String getSuffix() {
		return config.getSuffix().isEnabled() ? config.getSuffix().get() : "";
	}
}
