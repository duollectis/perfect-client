package com.perfect.client.functional.function.combat.tntAura.data;

import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.functional.function.combat.tntAura.TntAura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

@Getter
@AllArgsConstructor
public class TntInfo {

	private final BlockPos tntPosition;
	private final BlockHitResult tntPlaceResult;
	private final BlockHitResult tntInteractResult;
	private final List<PlaceInfo> trapPositions;
	private final TntAura.StatePriority statePriority;
	private final int trapSize;
}
