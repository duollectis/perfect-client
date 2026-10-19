package com.perfect.client.manager.game.targetManager;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import net.minecraft.entity.LivingEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.perfect.client.manager.system.FunctionManager.attackAura;
import static com.perfect.client.manager.system.FunctionManager.autoCrystal;

public class TargetManager extends Manager {

	private final ConcurrentHashMap<Class<? extends Function>, LivingEntity> targets = new ConcurrentHashMap<>();

	@Override
	public void onPostTick() {
		targets.clear();

		add(AutoCrystal.class, autoCrystal);
		add(AttackAura.class, attackAura);
	}

	public LivingEntity getTarget(java.util.function.Function<Class<? extends Function>, Integer> priorityFunction) {
		LivingEntity bestTarget = null;
		int bestPriority = 0;

		for (Map.Entry<Class<? extends Function>, LivingEntity> entry : targets.entrySet()) {
			Class<? extends Function> function = entry.getKey();
			LivingEntity target = entry.getValue();

			if (function == null || target == null) {
				continue;
			}

			int priority = priorityFunction.apply(function);

			if (priority < 0) {
				continue;
			}

			if (priority > bestPriority || bestTarget == null) {
				bestTarget = target;
				bestPriority = priority;
			}
		}

		return bestTarget;
	}

	private <T extends Function & ITarget, K extends Class<T>> void add(K clazz, T function) {
		if (function.isEnabled()) {
			LivingEntity target = function.getTarget();

			if (target != null) {
				targets.put(clazz, target);
			}
		}
	}
}
