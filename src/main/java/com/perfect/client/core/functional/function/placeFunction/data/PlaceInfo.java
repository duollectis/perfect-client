package com.perfect.client.core.functional.function.placeFunction.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

@Getter
@Setter
@AllArgsConstructor
public class PlaceInfo {

	private BlockPos position;
	private BlockHitResult hitResult;
}