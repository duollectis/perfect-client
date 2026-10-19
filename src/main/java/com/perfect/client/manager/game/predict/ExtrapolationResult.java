package com.perfect.client.manager.game.predict;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor
public class ExtrapolationResult {

	private final Vec3d pos;
	private final Box box;
}
