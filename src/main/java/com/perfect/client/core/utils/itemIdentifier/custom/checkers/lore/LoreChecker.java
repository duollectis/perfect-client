package com.perfect.client.core.utils.itemIdentifier.custom.checkers.lore;

import com.perfect.client.core.utils.itemIdentifier.custom.CustomItemChecker;
import com.perfect.client.core.utils.text.StringUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LoreChecker extends CustomItemChecker<String> {

	public LoreChecker(String... components) {
		super(components);
	}

	@Override
	public boolean check(ItemStack stack) {
		LoreComponent lore = stack.get(DataComponentTypes.LORE);

		if (lore == null || lore.lines().isEmpty()) {
			return false;
		}

		List<String> stringsNotFound = new ArrayList<>(components);

		for (Text text : lore.lines()) {
			String string = StringUtils.textToString(text);
			stringsNotFound.remove(string);
		}

		return stringsNotFound.isEmpty();
	}
}
