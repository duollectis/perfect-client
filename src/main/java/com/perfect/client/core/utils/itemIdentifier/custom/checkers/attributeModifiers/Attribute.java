package com.perfect.client.core.utils.itemIdentifier.custom.checkers.attributeModifiers;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

public record Attribute(
		RegistryEntry<EntityAttribute> attribute,
		double amount,
		AttributeModifierSlot slot,
		EntityAttributeModifier.Operation operation
) {

	public static Attribute addValue(RegistryEntry<EntityAttribute> attribute, double amount) {
		return new Attribute(
				attribute,
				amount,
				AttributeModifierSlot.OFFHAND,
				EntityAttributeModifier.Operation.ADD_VALUE
		);
	}

	public static Attribute addPercent(RegistryEntry<EntityAttribute> attribute, double amount) {
		return new Attribute(
				attribute,
				amount,
				AttributeModifierSlot.OFFHAND,
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE
		);
	}
}
