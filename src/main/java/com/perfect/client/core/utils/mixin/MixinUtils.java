package com.perfect.client.core.utils.mixin;

import net.minecraft.entity.Entity;

import static com.perfect.client.PerfectClient.mc;

public class MixinUtils {

	
	public static boolean isClientPlayer(Object o) {
		return mc.player == o;
	}

	
	public static Entity toEntity(Object o) {
		return (Entity) o;
	}
}
