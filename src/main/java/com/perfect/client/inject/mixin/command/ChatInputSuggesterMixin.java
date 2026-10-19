package com.perfect.client.inject.mixin.command;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import com.perfect.client.core.manager.Managers;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

import static com.perfect.client.PerfectClient.prefix;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggesterMixin {

	@Shadow
	@Final
	TextFieldWidget textField;

	@Shadow
	boolean completingSuggestions;

	@Shadow
	private ParseResults<CommandSource> parse;

	@Shadow
	private CompletableFuture<Suggestions> pendingSuggestions;

	@Shadow
	private ChatInputSuggestor.SuggestionWindow window;

	@Shadow
	protected abstract void showCommandSuggestions();

	@Inject(
			method = "refresh",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/brigadier/StringReader;canRead()Z",
					remap = false
			),
			cancellable = true
	)
	public void perfect$onRefresh(CallbackInfo ci, @Local StringReader reader) {
		int length = prefix.length();

		if (reader.canRead(length) && reader.getString().startsWith(prefix, reader.getCursor())) {
			reader.setCursor(reader.getCursor() + length);

			if (parse == null) {
				parse = Managers.COMMAND.DISPATCHER.parse(reader, Managers.COMMAND.COMMAND_SOURCE);
			}

			int cursor = textField.getCursor();

			if (cursor >= 1 && (window == null || !completingSuggestions)) {
				pendingSuggestions = Managers.COMMAND.DISPATCHER.getCompletionSuggestions(parse, cursor);
				pendingSuggestions.thenRun(() -> {
					if (pendingSuggestions.isDone()) {
						showCommandSuggestions();
					}
				});
			}

			ci.cancel();
		}
	}
}
