package com.perfect.client.inject.mixin.module.betterChat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.sugar.Local;
import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.chat.MessageReceiveEvent;
import com.perfect.client.core.render.animation.AnimationUtils;
import com.perfect.client.inject.mixinInterface.IChatHud;
import com.perfect.client.inject.mixinInterface.IChatHudLine;
import com.perfect.client.inject.mixinInterface.IMessageHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.betterChat;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IChatHud {

	@Shadow
	@Final
	private MinecraftClient client;

	@Unique
	private boolean skipOnAddMessage;

	@Unique
	private int perfect$id;

	@Shadow
	@Final
	public List<ChatHudLine.Visible> visibleMessages;

	@Shadow
	@Final
	private List<ChatHudLine> messages;

	@Shadow
	public abstract void addMessage(
			Text message,
			@Nullable MessageSignatureData signatureData,
			@Nullable MessageIndicator indicator
	);

	@Shadow
	public abstract void addMessage(Text message);

	@Override
	public void perfect$add(Text message, int id) {
		perfect$id = id;
		addMessage(message);
		perfect$id = 0;
	}

	@Inject(
			method = "addVisibleMessage",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(ILjava/lang/Object;)V",
					shift = At.Shift.AFTER
			)
	)
	private void perfect$onAddMessageAfterNewChatHudLineVisible(ChatHudLine message, CallbackInfo ci) {
		((IChatHudLine) (Object) visibleMessages.getFirst()).perfect$setId(perfect$id);
	}

	@Inject(
			method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;add(ILjava/lang/Object;)V",
					shift = At.Shift.AFTER
			)
	)
	private void perfect$onAddMessageAfterNewChatHudLine(ChatHudLine message, CallbackInfo ci) {
		((IChatHudLine) (Object) messages.getFirst()).perfect$setId(perfect$id);
	}

	@SuppressWarnings("DataFlowIssue")
	@ModifyExpressionValue(
			method = "addVisibleMessage",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/text/OrderedText;Lnet/minecraft/client/gui/hud/MessageIndicator;Z)Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;"
			)
	)
	private ChatHudLine.Visible perfect$modifyChatHudLineVisible(
			ChatHudLine.Visible line,
			@Local(ordinal = 1) int j
	) {
		IMessageHandler handler = (IMessageHandler) client.getMessageHandler();
		if (handler == null) {
			return line;
		}

		IChatHudLine iChatHudLine = (IChatHudLine) (Object) line;

		iChatHudLine.perfect$setSender(handler.perfect$getSender());
		iChatHudLine.perfect$setStartOfEntry(j == 0);
		iChatHudLine.perfect$setX(-client.textRenderer.getWidth(iChatHudLine.perfect$getText()));

		return line;
	}

	@SuppressWarnings("DataFlowIssue")
	@ModifyExpressionValue(
			method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
			at = @At(
					value = "NEW",
					target = "(ILnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)Lnet/minecraft/client/gui/hud/ChatHudLine;"
			)
	)
	private ChatHudLine perfect$modifyChatHudLine(ChatHudLine line) {
		IMessageHandler handler = (IMessageHandler) client.getMessageHandler();
		if (handler == null) {
			return line;
		}

		IChatHudLine iChatHudLine = (IChatHudLine) (Object) line;

		iChatHudLine.perfect$setSender(handler.perfect$getSender());
		iChatHudLine.perfect$setX(-client.textRenderer.getWidth(iChatHudLine.perfect$getText()));

		return line;
	}

	@Inject(
			at = @At("HEAD"),
			method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
			cancellable = true
	)
	private void perfect$onAddMessage(
			Text message,
			MessageSignatureData signatureData,
			MessageIndicator indicator,
			CallbackInfo ci
	) {
		if (skipOnAddMessage) {
			return;
		}

		MessageReceiveEvent event = new MessageReceiveEvent(message, indicator, false);
		PerfectClient.EVENT_MANAGER.onMessageReceive(event);

		if (event.isCancelled()) {
			ci.cancel();
		}

		visibleMessages.removeIf(msg -> ((IChatHudLine) (Object) msg).perfect$getId() == perfect$id && perfect$id != 0);

		if (perfect$id != 0) {
			messages.removeIf(msg -> (((IChatHudLine) (Object) msg)).perfect$getId() == perfect$id);
		}

		if (event.isModified()) {
			ci.cancel();

			skipOnAddMessage = true;
			addMessage(event.getMessage(), signatureData, event.getIndicator());
			skipOnAddMessage = false;
		}
	}

	@SuppressWarnings("DataFlowIssue")
	@ModifyArg(
			method = "method_71991",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V"
			),
			index = 2
	)
	private int perfect$animateChatX(int originalX, @Local(argsOnly = true) ChatHudLine.Visible line) {
		IChatHudLine iLine = (IChatHudLine) (Object) line;
		float currentX = iLine.perfect$getX();

		if (currentX >= 0 || !betterChat.isAnimation()) {
			return originalX;
		}

		float newX = AnimationUtils.animateFloat(currentX, 0f, 0.1);

		if (newX >= -0.5f) {
			newX = 0f;
		}

		iLine.perfect$setX(newX);

		return originalX + (int) newX;
	}

	@ModifyExpressionValue(
			method = "addMessage(Lnet/minecraft/client/gui/hud/ChatHudLine;)V",
			at = @At(
					value = "CONSTANT",
					args = "intValue=100"
			)
	)
	private int perfect$maxLength(int size) {
		if (!betterChat.getExtraChatLength()) {
			return size;
		}

		return size + betterChat.getExtraChatLines();
	}

	@ModifyExpressionValue(
			method = "addVisibleMessage",
			at = @At(
					value = "CONSTANT",
					args = "intValue=100"
			)
	)
	private int perfect$maxLengthVisible(int size) {
		if (!betterChat.getExtraChatLength()) {
			return size;
		}

		return size + betterChat.getExtraChatLines();
	}

	@ModifyExpressionValue(
			method = "getWidth(D)I",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/math/MathHelper;floor(D)I"
			)
	)
	private static int perfect$modifyWidth(int width) {
		return betterChat.modifyChatWidth(width);
	}

	@ModifyReceiver(
			method = "method_71991",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)V"
			)
	)
	private DrawContext perfect$beforeDrawTextWithShadow(
			DrawContext instance,
			TextRenderer textRenderer,
			OrderedText text,
			int x,
			int y,
			int color,
			@Local(argsOnly = true) ChatHudLine.Visible line
	) {
		betterChat.beforeDrawMessage(instance, line, y, color);

		return instance;
	}

	@Inject(
			method = "method_71991",
			at = @At("TAIL")
	)
	private void perfect$afterDrawTextWithShadow(
			int i,
			DrawContext context,
			float f,
			int j,
			int k,
			int l,
			ChatHudLine.Visible visible,
			int m,
			float g,
			CallbackInfo info
	) {
		betterChat.afterDrawMessage(context);
	}

	@Inject(
			method = "clear",
			at = @At("HEAD"),
			cancellable = true
	)
	private void perfect$onClear(boolean clearHistory, CallbackInfo ci) {
		if (betterChat.getKeepHistory()) {
			ci.cancel();
		}
	}

	@ModifyExpressionValue(
			method = "method_71992",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/ChatHudLine$Visible;indicator()Lnet/minecraft/client/gui/hud/MessageIndicator;"
			)
	)
	private MessageIndicator onRender_modifyIndicator(MessageIndicator indicator) {
		if (betterChat.isEnabled()) {
			return null;
		}

		return indicator;
	}
}