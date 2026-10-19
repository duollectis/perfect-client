package com.perfect.client.functional.function.visuals.legacyPotions;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.entity.effect.StatusEffects;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LegacyPotions extends Function {

	public static Map<Object, Integer> legacyPotionColors = getColorMap();

	public LegacyPotions() {
		super("legacy-potions", "Legacy Potions", Category.VISUALS);
	}

	private static Map<Object, Integer> getColorMap() {
		return Stream.of(
				new Object[]{StatusEffects.SPEED.value(), 8171462},
				new Object[]{StatusEffects.SLOWNESS.value(), 5926017},
				new Object[]{StatusEffects.HASTE.value(), 14270531},
				new Object[]{StatusEffects.MINING_FATIGUE.value(), 4866583},
				new Object[]{StatusEffects.STRENGTH.value(), 9643043},
				new Object[]{StatusEffects.INSTANT_HEALTH.value(), 16262179},
				new Object[]{StatusEffects.INSTANT_DAMAGE.value(), 4393481},
				new Object[]{StatusEffects.JUMP_BOOST.value(), 2293580},
				new Object[]{StatusEffects.NAUSEA.value(), 5578058},
				new Object[]{StatusEffects.REGENERATION.value(), 13458603},
				new Object[]{StatusEffects.RESISTANCE.value(), 10044730},
				new Object[]{StatusEffects.FIRE_RESISTANCE.value(), 14981690},
				new Object[]{StatusEffects.WATER_BREATHING.value(), 3035801},
				new Object[]{StatusEffects.INVISIBILITY.value(), 8356754},
				new Object[]{StatusEffects.BLINDNESS.value(), 2039587},
				new Object[]{StatusEffects.NIGHT_VISION.value(), 2039713},
				new Object[]{StatusEffects.HUNGER.value(), 5797459},
				new Object[]{StatusEffects.WEAKNESS.value(), 4738376},
				new Object[]{StatusEffects.POISON.value(), 5149489},
				new Object[]{StatusEffects.WITHER.value(), 3484199},
				new Object[]{StatusEffects.HEALTH_BOOST.value(), 16284963},
				new Object[]{StatusEffects.ABSORPTION.value(), 2445989},
				new Object[]{StatusEffects.SATURATION.value(), 16262179},
				new Object[]{StatusEffects.GLOWING.value(), 9740385},
				new Object[]{StatusEffects.LEVITATION.value(), 13565951},
				new Object[]{StatusEffects.LUCK.value(), 3381504},
				new Object[]{StatusEffects.UNLUCK.value(), 12624973},
				new Object[]{StatusEffects.SLOW_FALLING.value(), 16773073},
				new Object[]{StatusEffects.CONDUIT_POWER.value(), 1950417},
				new Object[]{StatusEffects.DOLPHINS_GRACE.value(), 8954814},
				new Object[]{StatusEffects.BAD_OMEN.value(), 745784},
				new Object[]{StatusEffects.HERO_OF_THE_VILLAGE.value(), 4521796},
				new Object[]{StatusEffects.DARKNESS.value(), 2696993}
		).collect(Collectors.toMap(mapper -> mapper[0], mapper -> (Integer) mapper[1]));
	}
}
