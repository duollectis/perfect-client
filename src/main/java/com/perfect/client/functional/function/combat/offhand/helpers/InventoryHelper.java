package com.perfect.client.functional.function.combat.offhand.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.functional.function.combat.offhand.Offhand;
import com.perfect.client.functional.function.combat.offhand.OffhandConfig;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;

import static com.perfect.client.manager.system.FunctionManager.suicide;


public class InventoryHelper extends FunctionHelper<Offhand, OffhandConfig> {

	public InventoryHelper(Offhand offhand, OffhandConfig config) {
		super(offhand, config);
	}

	public boolean isGoodStack(ItemStack stack, Item searchingItem) {
		if (suicide.isEnabled() && searchingItem.equals(Items.TOTEM_OF_UNDYING)) {
			return !stack.isOf(Items.TOTEM_OF_UNDYING);
		}
		return stack.isOf(searchingItem);
	}

	public Item getItem() {
		if (mc.player == null) {
			return Items.TOTEM_OF_UNDYING;
		}

		if (shouldGapple()) {
			Item gapple = getGappleItem();

			if (gapple != null) {
				return gapple;
			}
		}

		if (!isSafe()) {
			return Items.TOTEM_OF_UNDYING;
		}

		return config.getOffhandItem().get().getItem();
	}

	private boolean isSafe() {
		if (!isTotemFound()) {
			return true;
		}

		return function.getSafetyHelper().isSafe();
	}

	private boolean isTotemFound() {
		return InvUtils.find(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).found();
	}

	private Item getGappleItem() {
		if (mc.player == null)
			return null;

		FindItemResult enchanted = InvUtils.findFromEnd(stack -> stack.isOf(Items.ENCHANTED_GOLDEN_APPLE));

		if (enchanted.found()) {
			return Items.ENCHANTED_GOLDEN_APPLE;
		}

		FindItemResult golden = InvUtils.findFromEnd(stack -> stack.isOf(Items.GOLDEN_APPLE));

		if (golden.found()) {
			return Items.GOLDEN_APPLE;
		}

		return null;
	}

	private boolean shouldGapple() {
		if (mc.player == null) {
			return false;
		}
		if (mc.currentScreen != null) {
			return false;
		}
		if (!config.isGAppleBindPressed()) {
			return false;
		}
		return isWeapon(mc.player.getMainHandStack());
	}

	private boolean isWeapon(ItemStack stack) {
		return stack.isIn(ItemTags.SWORDS) || stack.getItem() instanceof AxeItem;
	}
}