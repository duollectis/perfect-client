package com.perfect.client.core.utils.itemIdentifier.custom.checkers.enchantment;

import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.itemIdentifier.custom.CustomItemChecker;
import net.minecraft.item.ItemStack;

public class EnchantmentChecker extends CustomItemChecker<EnchantmentLevel> {

	public EnchantmentChecker(EnchantmentLevel... components) {
		super(components);
	}

	@Override
	public boolean check(ItemStack stack) {
		for (EnchantmentLevel enchantment : components) {
			if (EnchantmentUtils.getLevel(stack, enchantment.enchantment()) != enchantment.level()) {
				return false;
			}
		}
		return true;
	}
}
