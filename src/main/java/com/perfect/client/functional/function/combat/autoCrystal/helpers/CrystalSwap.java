package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.utils.functions.inventory.Swap;

public enum CrystalSwap {
	HOTBAR,
	ALTERNATIVE,
	CC;

	public Swap convert() {
		return switch (this) {
			case HOTBAR, CC -> Swap.HOTBAR;
			case ALTERNATIVE -> Swap.ALTERNATIVE;
		};
	}
}
