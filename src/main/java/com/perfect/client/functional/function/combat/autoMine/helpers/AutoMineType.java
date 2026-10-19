package com.perfect.client.functional.function.combat.autoMine.helpers;

import com.perfect.client.core.utils.text.INameable;
import lombok.AllArgsConstructor;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor
public enum AutoMineType implements INameable {
	BURROW("Burrow"),
	CITY("City"),
	SURROUND_CEV("Surround Cev"),
	FAR_CEV("Far Trap Cev"),
	TRAP_CEV("Trap Cev"),
	NONE("None");

	public static final AutoMineType[] values = {BURROW, CITY, SURROUND_CEV, FAR_CEV, TRAP_CEV};

	private final String name;

	public BlockPos additionalSupport(BlockPos minePos) {
		if (this == SURROUND_CEV || this == CITY)
			return minePos.down();
		return null;
	}

	@Override
	public String getName() {
		return name;
	}
}