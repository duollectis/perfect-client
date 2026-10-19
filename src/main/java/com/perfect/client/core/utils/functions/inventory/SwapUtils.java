package com.perfect.client.core.utils.functions.inventory;

import com.perfect.client.core.manager.Managers;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

import static com.perfect.client.PerfectClient.mc;

public class SwapUtils {

	public static SwapInfo find(Swap swapType, Item item) {
		return find(swapType, InvUtils.find(item));
	}

	public static SwapInfo find(Swap swapType, int slot) {
		FindItemResult result = getFindItemResult(slot);
		return find(swapType, result);
	}

	public static SwapInfo find(Swap swapType, FindItemResult result) {
		if (mc.player == null || mc.interactionManager == null || result == null || !result.found()) {
			return SwapInfo.NOT_FOUND;
		}

		if (mc.player.getOffHandStack().getItem().equals(result.stack().getItem())) {
			return handInfo(Hand.OFF_HAND, result);
		}

		if (InvUtils.getStack(Managers.NETWORK.getSlot()).getItem().equals(result.stack().getItem())) {
			return handInfo(Hand.MAIN_HAND, result);
		}

		if (!result.found()) {
			return SwapInfo.NOT_FOUND;
		}

		if (!swapType.isSlotNormal(result.slot())) {
			return SwapInfo.NOT_FOUND;
		}

		int selected = mc.player.getInventory().getSelectedSlot();

		return new SwapInfo(result, swapType, true, true, false, selected);
	}

	public static SwapInfo swap(Swap swapType, Item item) {
		return swap(swapType, InvUtils.find(item));
	}

	public static SwapInfo swap(Swap swapType, int slot) {
		return find(swapType, slot);
	}

	public static SwapInfo swap(Swap swapType, FindItemResult result) {
		SwapInfo info = find(swapType, result);
		info.swap();
		return info;
	}

	private static SwapInfo handInfo(Hand hand, FindItemResult result) {
		return new SwapInfo(result, null, true, false, hand == Hand.OFF_HAND, 0);
	}

	private static FindItemResult getFindItemResult(int slot) {
		return new FindItemResult(InvUtils.getStack(slot), slot);
	}
}
