package com.perfect.client.core.utils.itemIdentifier;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Supplier;

public abstract class ItemIdentifier {

	@Getter
	private final ItemStack defaultStack;

	@Getter
	private final String id;
	private final Supplier<String> nameSupplier;

	@Getter
	protected int priority = 0;

	public ItemIdentifier(ItemStack defaultStack, String id, Supplier<String> nameSupplier) {
		this.defaultStack = defaultStack;
		this.id = id;
		this.nameSupplier = nameSupplier;
	}

	public String getName() {
		return nameSupplier.get();
	}

	public abstract boolean isSuitableStack(ItemStack stack);

	public ItemIdentifier of(Item item) {
		return new DefaultItemIdentifier(item);
	}
}
