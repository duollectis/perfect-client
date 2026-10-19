package com.perfect.client.core.utils.functions.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static com.perfect.client.PerfectClient.mc;


public record FindItemResult(
		ItemStack stack,
		int slot
) {

	public static final FindItemResult NOT_FOUND = new FindItemResult(null, -1);

	public boolean found() {
		return slot != -1;
	}

	public Hand getHand() {
		if (mc.player == null) {
			return Hand.MAIN_HAND;
		}

		if (slot == 45) {
			return Hand.OFF_HAND;
		}

		else if (slot == mc.player.getInventory().getSelectedSlot()) {
			return Hand.MAIN_HAND;
		}

		return null;
	}

	public boolean isMainHand() {
		return getHand() == Hand.MAIN_HAND;
	}

	public boolean isOffhand() {
		return getHand() == Hand.OFF_HAND;
	}

	public boolean isHotbar() {
		return slot >= 0 && slot <= 8;
	}

	public boolean isMain() {
		return slot >= 9 && slot <= 35;
	}

	public boolean isArmor() {
		return slot >= 36 && slot <= 39;
	}

	public static FindItemResult fromSelectedSlot() {
		if (mc.player == null) {
			return NOT_FOUND;
		}

		int slot = mc.player.getInventory().getSelectedSlot();
		ItemStack stack = mc.player.getInventory().getStack(slot);

		return new FindItemResult(stack, slot);
	}
}
