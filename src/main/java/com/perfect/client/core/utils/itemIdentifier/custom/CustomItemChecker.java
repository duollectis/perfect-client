package com.perfect.client.core.utils.itemIdentifier.custom;

import lombok.Getter;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public abstract class CustomItemChecker<T> {

	protected final List<T> components;

	@Getter
	private final int size;

	@SafeVarargs
	public CustomItemChecker(T... components) {
		this.components = Arrays.asList(components);
		size = this.components.size();
	}

	public abstract boolean check(ItemStack stack);

}
