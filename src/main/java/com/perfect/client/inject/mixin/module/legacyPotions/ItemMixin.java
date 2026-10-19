package com.perfect.client.inject.mixin.module.legacyPotions;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.perfect.client.manager.system.FunctionManager.legacyPotions;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(
			method = "hasGlint",
			at = @At("HEAD"),
			cancellable = true
	)
	public void perfect$hasGlint(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof PotionItem) {
			PotionContentsComponent potionContentsComponent = stack.getOrDefault(
					DataComponentTypes.POTION_CONTENTS,
					PotionContentsComponent.DEFAULT
			);

			cir.setReturnValue(legacyPotions.isEnabled() && !potionContentsComponent.matches(Potions.WATER));
		}
	}
}
