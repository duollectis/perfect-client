package com.perfect.client.functional.function.visuals.auctionHelper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;

@Getter
@AllArgsConstructor
public enum PickaxeEnchantments {
	EFFICIENCY(Enchantments.EFFICIENCY),
	UNBREAKING(Enchantments.UNBREAKING),
	FORTUNE(Enchantments.FORTUNE),
	SILK_TOUCH(Enchantments.SILK_TOUCH),
	MENDING(Enchantments.MENDING);

	private final RegistryKey<Enchantment> enchantment;
}