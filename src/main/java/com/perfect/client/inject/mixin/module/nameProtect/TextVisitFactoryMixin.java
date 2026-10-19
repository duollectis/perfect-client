package com.perfect.client.inject.mixin.module.nameProtect;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.perfect.client.manager.system.FunctionManager.nameProtect;

@Mixin(TextVisitFactory.class)
public class TextVisitFactoryMixin {

	@ModifyArg(
			method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
					ordinal = 0
			),
			index = 0
	)
	private static String perfect$adjustText(String text) {
		if (nameProtect.isEnabled()) {
			return nameProtect.modify(text);
		}

		return text;
	}
}
