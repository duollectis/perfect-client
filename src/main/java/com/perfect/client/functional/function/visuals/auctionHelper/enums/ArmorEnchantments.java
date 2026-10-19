package com.perfect.client.functional.function.visuals.auctionHelper.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKey;

@Getter
@AllArgsConstructor
public enum ArmorEnchantments {
	PROTECTION(Enchantments.PROTECTION),
	UNBREAKING(Enchantments.UNBREAKING),
	MENDING(Enchantments.MENDING),
	NOT_THORNS(Enchantments.THORNS);

	private final RegistryKey<Enchantment> enchantment;
}