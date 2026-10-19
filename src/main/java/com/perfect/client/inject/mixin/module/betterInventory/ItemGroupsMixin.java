package com.perfect.client.inject.mixin.module.betterInventory;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.item.ItemGroups;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.perfect.client.manager.system.FunctionManager.betterInventory;

@Mixin(ItemGroups.class)
public class ItemGroupsMixin {

	@ModifyReturnValue(
			method = "updateDisplayContext",
			at = @At("RETURN")
	)
	private static boolean modifyReturn(boolean original) {
		return original || betterInventory.updateTooltips();
	}
}