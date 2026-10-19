package com.perfect.client.core.utils.functions.inventory;

import com.perfect.client.core.utils.text.ITranslatable;


public enum Swap implements ITranslatable {
	HOTBAR,
	ALTERNATIVE;

	public boolean isSlotNormal(int slot) {
		if (slot == -1) {
			return false;
		}

		if (this == HOTBAR) {
			return SlotUtils.isHotbar(slot);
		}

		return true;
	}

	@Override
	public String getTranslationId() {
		return "swap";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
