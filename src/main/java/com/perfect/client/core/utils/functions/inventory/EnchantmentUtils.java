package com.perfect.client.core.utils.functions.inventory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

import static com.perfect.client.PerfectClient.mc;


public class EnchantmentUtils {

	public static void add(ItemStack stack, RegistryKey<Enchantment> enchantment, int level) {
		stack.addEnchantment(getEntry(enchantment), level);
	}

	public static int getLevel(ItemStack stack, RegistryKey<Enchantment> enchantment) {
		return EnchantmentHelper.getLevel(getEntry(enchantment), stack);
	}

	public static RegistryEntry<Enchantment> getEntry(RegistryKey<Enchantment> registryKey) {
		if (mc.world == null) {
			return null;
		}

		return mc.world
				.getRegistryManager()
				.getOrThrow(RegistryKeys.ENCHANTMENT)
				.streamEntries()
				.filter(entry -> entry.matchesKey(registryKey)).findFirst().orElse(null);
	}
}
