package com.perfect.client.core.utils.itemIdentifier.custom;

import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class CustomItemIdentifier extends ItemIdentifier {

	private final Item item;
	private final List<CustomItemChecker<?>> checkers;

	public CustomItemIdentifier(Item item, String id, String name, boolean glint, CustomItemChecker<?>... checkers) {
		this(item, id, name, glint, item.getDefaultStack().getMaxCount(), checkers);
	}

	public CustomItemIdentifier(
			Item item,
			String id,
			String name,
			boolean glint,
			int maxCount,
			CustomItemChecker<?>... checkers
	) {
		super(getDefaultStack(item, glint, maxCount), id, () -> name);
		this.checkers = Arrays.asList(checkers);
		this.item = item;

		this.checkers.forEach(checker -> priority += checker.getSize());
	}

	@Override
	public boolean isSuitableStack(ItemStack stack) {
		if (!stack.isOf(item)) {
			return false;
		}

		for (CustomItemChecker<?> checker : checkers) {

			if (!checker.check(stack)) {
				return false;
			}
		}

		return true;
	}

	private static ItemStack getDefaultStack(Item item, boolean glint, int maxCount) {
		ItemStack stack = item.getDefaultStack();

		stack.set(DataComponentTypes.MAX_STACK_SIZE, maxCount);

		if (glint) {
			stack.addEnchantment(EnchantmentUtils.getEntry(Enchantments.UNBREAKING), 1);
		}

		return stack;
	}
}
