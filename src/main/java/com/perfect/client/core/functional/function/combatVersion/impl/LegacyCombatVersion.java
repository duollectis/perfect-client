package com.perfect.client.core.functional.function.combatVersion.impl;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionConfig;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionHandler;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.SecureRandomUtils;
import net.minecraft.entity.Entity;

import java.util.ArrayList;

public class LegacyCombatVersion<T extends Function, K extends FunctionConfig<T>> extends CombatVersionHandler<T, K> {

	private static final int RANDOMS_SIZE = 20;
	private static final int SIMULATE_TICKS = 10;

	private final ArrayList<Double> randoms = new ArrayList<>();

	private int clicksThisSecond = 0;
	private int tickTimer = 0;

	private int skipTimer;

	private boolean skipThisTick = false;

	public LegacyCombatVersion(T function, CombatVersionConfig config) {
		super(function, config);
	}

	@Override
	public void update() {
		fillRandoms();
	}

	@Override
	public void onPreTick() {
		if (tickTimer >= WorldUtils.TICKS_PER_SECOND) {
			clicksThisSecond = 0;
			tickTimer = 0;
		}
		else {
			tickTimer++;
		}

		if (skipTimer >= getSkipTimeTicks()) {
			skipTimer = 0;
			skipThisTick = false;
		}
		else {
			skipTimer++;
			skipThisTick = true;
		}
	}

	private int getSkipTimeTicks() {
		return (int) (WorldUtils.TICKS_PER_SECOND / config.getCpsLimit().get()) - 1;
	}

	@Override
	public void postAttack() {
		clicksThisSecond++;

		randoms.removeFirst();
		fillRandoms();
	}

	private void fillRandoms() {
		for (int i = 0; i < 20; i++) {
			if (randoms.size() >= RANDOMS_SIZE) {
				break;
			}

			randoms.add(SecureRandomUtils.random(0.0, 1.0));
		}
	}

	public boolean shouldAttack(Entity entity) {
		return shouldAttack(entity, clicksThisSecond, 0, skipThisTick, false);
	}

	
	private boolean shouldAttack(
			Entity entity,
			int clicksThisSecond,
			int simulateTicks,
			boolean skipThisTick,
			boolean isSimulation
	) {
		if (!isSimulation && config.getDamageTiming().get()) {
			if (isDamageTimingAttack(entity, 0)) {
				return true;
			}
			else if (needSkipForNextDamageTiming(entity)) {
				return false;
			}
		}

		if (skipThisTick) {
			return false;
		}
		if (clicksThisSecond >= config.getCpsLimit().get()) {
			return false;
		}
		return isHit(simulateTicks);
	}

	
	private boolean needSkipForNextDamageTiming(Entity entity) {
		int simulationClicksThisSecond = clicksThisSecond;
		int simulationTickTimer = tickTimer;
		int simulationSkipTimer = skipTimer;
		boolean simulationSkipThisTick;

		for (int i = 0; i < SIMULATE_TICKS; i++) {
			if (simulationTickTimer >= WorldUtils.TICKS_PER_SECOND) {
				simulationClicksThisSecond = 0;
				simulationTickTimer = 0;
			}
			else {
				simulationTickTimer++;
			}

			if (simulationSkipTimer >= getSkipTimeTicks()) {
				simulationSkipTimer = 0;
				simulationSkipThisTick = false;
			}
			else {
				simulationSkipTimer++;
				simulationSkipThisTick = true;
			}

			boolean isAttack = shouldAttack(
					entity,
					simulationClicksThisSecond,
					i,
					simulationSkipThisTick,
					true
			);

			if (isAttack) {
				simulationClicksThisSecond++;
			}

			if (isDamageTimingAttack(entity, i)) {
				return !isAttack;
			}
			else if (isAttack) {
				return false;
			}
		}
		return false;
	}

	private boolean isHit(int simulateTicks) {
		return config.getHitChance().get() / 100.0 >= randoms.get(simulateTicks);
	}

	private boolean isDamageTimingAttack(Entity entity, int simulateTicks) {
		int hurtTime = EntityUtils.getHurtTime(entity) - simulateTicks - Managers.PING.getPingInTicks(false);

		return hurtTime <= 0;
	}
}
