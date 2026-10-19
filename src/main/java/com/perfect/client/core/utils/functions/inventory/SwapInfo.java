package com.perfect.client.core.utils.functions.inventory;

import com.perfect.client.core.utils.functions.interact.InteractUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;

import static com.perfect.client.PerfectClient.mc;

@Getter
@AllArgsConstructor

public class SwapInfo {

	public static final SwapInfo NOT_FOUND = new SwapInfo(
			null,
			null,
			false,
			false,
			false,
			0
	);

	private FindItemResult result;
	private Swap swapType;
	private boolean found;
	private boolean needSwap;
	private boolean offhand;
	private int selected;

	public InteractUtils.Argument[] getPlaceArgs() {
		return new InteractUtils.Argument[]{
				InteractUtils.Argument.NO_ITEM,
				offhand ? InteractUtils.Argument.OFF_HAND : null
		};
	}

	public Hand getHand() {
		if (offhand) {
			return Hand.OFF_HAND;
		}
		else {
			return Hand.MAIN_HAND;
		}
	}


	public void swap() {
		if (!found || !needSwap) {
			return;
		}

		switch (swapType) {
			case HOTBAR -> hotbarSwap(result.slot());
			case ALTERNATIVE -> altSwap();
		}
	}

	public void swapBack() {
		if (!found
				|| !needSwap
				|| mc.player == null
				|| mc.interactionManager == null
		) {
			return;
		}

		switch (swapType) {
			case HOTBAR -> hotbarSwap(selected);
			case ALTERNATIVE -> altSwap();
		}
	}

	private void hotbarSwap(int slot) {
		InvUtils.setSlot(slot, false);
	}

	private void altSwap() {
		InvUtils.clickSlot(SlotUtils.getSlotID(result.slot()), selected, SlotActionType.SWAP);
	}
}