package com.perfect.client.functional.function.combat.autoArmor;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.SlotActionType;

public class AutoArmor extends Function {

	private final AutoArmorConfig config = new AutoArmorConfig(this);
	public final TimerUtils timerUtils = new TimerUtils();

	public AutoArmor() {
		super("auto-armor", "Auto Armor", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		timerUtils.reset();
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) {
			return;
		}

		if (!timerUtils.passedTicks(config.getDelay().get())) {
			return;
		}

		boolean actionDone = false;

		for (int i = 0; i < 4; i++) {
			EquipmentSlot slot = getSlotFromInt(i);
			ItemStack currentArmor = mc.player.getEquippedStack(slot);

			if (slot == EquipmentSlot.CHEST && config.getIgnoreElytra().get() && currentArmor.isOf(Items.ELYTRA)) {
				continue;
			}

			int bestInvSlot = -1;
			float bestScore = getScore(currentArmor);

			for (int j = 0; j < 36; j++) {
				ItemStack invItem = mc.player.getInventory().getStack(j);
				if (invItem.isEmpty()) {
					continue;
				}

				if (!canEquipInSlot(invItem, slot)) {
					continue;
				}

				float score = getScore(invItem);
				if (score > bestScore) {
					bestScore = score;
					bestInvSlot = j;
				}
			}

			if (bestInvSlot != -1) {
				InvUtils.clickSlot(SlotUtils.getSlotID(bestInvSlot), 0, SlotActionType.QUICK_MOVE);
				actionDone = true;
				break;
			}
		}

		if (actionDone) {
			timerUtils.reset();
		}
	}

	private boolean canEquipInSlot(ItemStack stack, EquipmentSlot slot) {
		EquippableComponent equippable = stack.get(DataComponentTypes.EQUIPPABLE);

		return equippable != null && equippable.slot() == slot;
	}

	private float getScore(ItemStack stack) {
		if (stack.isEmpty())
			return -1f;

		float score = 0;

		AttributeModifiersComponent modifiers = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
		if (modifiers != null) {
			for (AttributeModifiersComponent.Entry entry : modifiers.modifiers()) {
				if (entry.attribute().equals(EntityAttributes.ARMOR)) {
					score += (float) entry.modifier().value() * 5;
				}
				if (entry.attribute().equals(EntityAttributes.ARMOR_TOUGHNESS)) {
					score += (float) entry.modifier().value() * 2;
				}
			}
		}

		ItemEnchantmentsComponent enchantments = stack.getOrDefault(
				DataComponentTypes.ENCHANTMENTS,
				ItemEnchantmentsComponent.DEFAULT
		);
		for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
			RegistryKey<Enchantment> key = entry.getKey().getKey().orElse(null);
			if (key == null) {
				continue;
			}

			int level = entry.getIntValue();

			if (key == Enchantments.PROTECTION) {
				score += level * 3;
			}
			else if (key == Enchantments.MENDING) {
				score += level * 2;
			}
			else if (key == Enchantments.UNBREAKING) {
				score += level * 0.5f;
			}
		}

		return score;
	}

	private EquipmentSlot getSlotFromInt(int i) {
		return switch (i) {
			case 0 -> EquipmentSlot.FEET;
			case 1 -> EquipmentSlot.LEGS;
			case 2 -> EquipmentSlot.CHEST;
			case 3 -> EquipmentSlot.HEAD;
			default -> EquipmentSlot.MAINHAND;
		};
	}
}