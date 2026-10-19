package com.perfect.client.core.utils.itemIdentifier.custom.checkers.attributeModifiers;

import com.perfect.client.core.utils.itemIdentifier.custom.CustomItemChecker;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AttributeModifiersChecker extends CustomItemChecker<Attribute> {

	public AttributeModifiersChecker(Attribute... components) {
		super(components);
	}

	@Override
	public boolean check(ItemStack stack) {
		AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);

		if (modifiers == null) {
			return false;
		}

		List<Attribute> notFoundAttributes = new ArrayList<>(components);

		for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {

			Attribute attribute = new Attribute(
					entry.attribute(),
					entry.modifier().value(),
					entry.slot(),
					entry.modifier().operation()
			);

			notFoundAttributes.remove(attribute);
		}

		return notFoundAttributes.isEmpty();
	}
}
