package com.perfect.client.inject.mixin.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.LoggerUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.PerfectClient.prefix;

@Mixin(Screen.class)
public class ScreenMixin {

	@Inject(
			method = "handleTextClick",
			at = @At(value = "TAIL"),
			cancellable = true
	)
	private void perfect$perfect$onRunCommand(Style style, CallbackInfoReturnable<Boolean> cir) {
		ClickEvent clickEvent = style.getClickEvent();

		if (clickEvent == null) {
			return;
		}

		if (clickEvent instanceof ClickEvent.RunCommand(String string)) {
			if (string.startsWith(prefix)) {
				try {
					Managers.COMMAND.dispatch(string.substring(prefix.length()));
					cir.setReturnValue(true);
				}
				catch (CommandSyntaxException e) {
					LoggerUtils.warning(e);
				}
			}
		}
	}
}
