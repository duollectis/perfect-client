package com.perfect.client.functional.function.combat.autoMine.data;

public record BlockCheckResult(
		boolean validBlock,
		boolean canPlaceCrystal
) {

	public static final BlockCheckResult INVALID = new BlockCheckResult(false, false);

	public boolean isInvalid() {
		return !validBlock;
	}
}
