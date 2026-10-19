package com.perfect.client.functional.function.player.autoBuy.utils;

import com.perfect.client.core.utils.ItemPrice;
import net.minecraft.item.ItemStack;

public record SellItem(
		ItemStack stack,
		ItemPrice price,
		int slot
) {
}
