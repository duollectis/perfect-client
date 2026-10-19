package com.perfect.client.manager.game.item;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.itemIdentifier.DefaultItemIdentifier;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager extends Manager {

	private static final List<ItemIdentifier> customItems = new ArrayList<>();
	private static final Map<Item, DefaultItemIdentifier> defaultItems = new HashMap<>();

	public ItemManager() {
		super();
	}

	public ItemIdentifier identifierOf(Item item) {
		return defaultItems.computeIfAbsent(item, i -> new DefaultItemIdentifier(item));
	}

	public ItemIdentifier identify(ItemStack stack) {
		ItemIdentifier customIdentifier = identifyCustom(stack);

		if (customIdentifier != null) {
			return customIdentifier;
		}

		return identifierOf(stack.getItem());
	}

	private ItemIdentifier identifyCustom(ItemStack stack) {
		ItemIdentifier identified = null;
		int bestPriority = 0;

		for (ItemIdentifier itemIdentifier : customItems) {
			if (itemIdentifier.isSuitableStack(stack)) {
				int priority = itemIdentifier.getPriority();

				if (identified == null || priority > bestPriority) {
					identified = itemIdentifier;
					bestPriority = priority;
				}
			}
		}

		return identified;
	}

	public ItemIdentifier getById(String id) {
		ItemIdentifier customItem = getCustomItemById(id);

		if (customItem != null) {
			return customItem;
		}

		return identifierOf(Registries.ITEM.get(Identifier.of(id)));
	}

	private ItemIdentifier getCustomItemById(String id) {
		for (ItemIdentifier item : customItems) {
			if (item.getId().equals(id)) {
				return item;
			}
		}
		return null;
	}

	public static ItemIdentifier add(ItemIdentifier itemIdentifier) {
		customItems.add(itemIdentifier);
		return itemIdentifier;
	}
}
