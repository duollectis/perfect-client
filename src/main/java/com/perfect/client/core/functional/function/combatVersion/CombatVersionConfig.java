package com.perfect.client.core.functional.function.combatVersion;

import com.perfect.client.core.utils.range.IntRange;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
@AllArgsConstructor
public class CombatVersionConfig {

	private final Supplier<IntRange> preAttackSprintDelay;
	private final Supplier<IntRange> postAttackSprintDelay;

	private final Supplier<Boolean> stopSprint;
	private final Supplier<Boolean> smartCrit;
	private final Supplier<Boolean> onlyCrit;

	private final Supplier<IntRange> attackDelay;
	private final Supplier<Double> attackDelayChance;
	private final Supplier<Boolean> perfectTiming;

	private final Supplier<Double> cpsLimit;
	private final Supplier<Boolean> damageTiming;
	private final Supplier<Double> hitChance;
}
