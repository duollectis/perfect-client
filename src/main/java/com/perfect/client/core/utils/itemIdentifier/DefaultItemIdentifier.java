package com.perfect.client.core.utils.itemIdentifier;

import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class DefaultItemIdentifier extends ItemIdentifier {

	private final Item item;

	public DefaultItemIdentifier(Item item) {
		super(
				item.getDefaultStack(),
				Registries.ITEM.getId(item).toString(),
				() -> StringUtils.textToString(item.getName())
		);

		this.item = item;
	}

	@Override
	public boolean isSuitableStack(ItemStack stack) {
		return stack.isOf(item);
	}
}
