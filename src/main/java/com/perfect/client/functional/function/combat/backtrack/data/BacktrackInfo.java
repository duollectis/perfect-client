package com.perfect.client.functional.function.combat.backtrack.data;

import lombok.Getter;
import net.minecraft.util.math.Box;

@Getter
public class BacktrackInfo {

	private final Box hitbox;
	private int ticks;

	public BacktrackInfo(Box hitbox) {
		this.hitbox = hitbox;
	}

	public void tick() {
		ticks++;
	}
}
