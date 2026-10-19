package com.perfect.client.core.functional.function.combatVersion.impl;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionConfig;
import com.perfect.client.core.functional.function.combatVersion.CombatVersionHandler;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.range.utils.BiasIntRangeGenerator;
import com.perfect.client.core.utils.range.utils.IntRangeGenerator;
import com.perfect.client.manager.game.predict.PredictManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class NewCombatVersion<T extends Function, K extends FunctionConfig<T>> extends CombatVersionHandler<T, K> {

	private final BiasIntRangeGenerator attackDelay = new BiasIntRangeGenerator(
			() -> config.getAttackDelay().get(),
			() -> config.getAttackDelayChance().get()
	);

	private final IntRangeGenerator preAttackSprintDelayGenerator = new IntRangeGenerator(
			() -> config.getPreAttackSprintDelay().get()
	);

	public NewCombatVersion(T function, CombatVersionConfig config) {
		super(function, config);
	}

	@Override
	public void update() {
		preAttackSprintDelayGenerator.update();
		attackDelay.update();
	}

	@Override
	public void postAttack() {
		if (config.getSmartCrit().get() && config.getStopSprint().get()) {
			Managers.PLAYER.setSprintDelay(config.getPostAttackSprintDelay().get());
		}

		update();
	}

	
	@Override
	public void onPostMovementInput(MovementInputEvent event) {
		if (mc.player == null) {
			return;
		}

		if (paused) {
			return;
		}

		if (config.getSmartCrit().get()
				&& config.getStopSprint().get()
				&& isAttackCooledDown(preAttackSprintDelayGenerator.getValue(), 0.9f)) {
			event.sprinting.set(false);
			mc.player.setSprinting(false);
		}
	}

	
	@Override
	public boolean shouldAttack(Entity entity) {
		if (mc.player == null || mc.world == null || mc.interactionManager == null) {
			return false;
		}

		if (config.getPerfectTiming().get()) {
			if (shouldSkipCrit()) {
				return isAttackCooledDown(0, 1.0f);
			}
			else if (isCrit()) {
				if (isAttackCooledDown(0, 1.0f)) {
					return true;
				}
				if (isCritNext()) {
					return false;
				}
				return isAttackCooledDown(0, 0.9f);
			}
		}

		if (!isAttackCooledDown(0, 0.9f)) {
			return false;
		}
		return shouldSkipCrit() || isCrit();
	}

	
	private boolean isCritNext() {
		if (mc.player == null) {
			return false;
		}

		Vec3d velocity = mc.player.getVelocity();
		double yVelocity = Math.min((velocity.y - PredictManager.GRAVITY) * 0.98, 0.0);

		Vec3d nextPos = mc.player.getPos().add(velocity.x, yVelocity, velocity.z);

		Box box = PositionMath.getBoxAt(mc.player.getBoundingBox(), nextPos);

		for (BlockPos pos : PositionMath.getIntersectedBlocks(box)) {
			if (BlockUtils.isCollidable(pos)) {
				return false;
			}
		}
		return true;
	}

	private boolean isAttackCooledDown(int predictTicks, float target) {
		return getAttackCooldownProgress(predictTicks) >= target;
	}

	
	public float getAttackCooldownProgress(int predictTicks) {
		if (mc.player == null) {
			return 0.0f;
		}

		float ticks = mc.player.lastAttackedTicks + predictTicks + 0.5f - attackDelay.getValue();
		float time = ticks / mc.player.getAttackCooldownProgressPerTick();

		return Math.clamp(time, 0.0f, 1.0f);
	}

	
	public int getTicksToAttack() {
		if (mc.player == null) {
			return 0;
		}

		float targetTicks = 0.9f * mc.player.getAttackCooldownProgressPerTick();
		float currentTicks = mc.player.lastAttackedTicks + 0.5f - attackDelay.getValue();

		int ticksToAttack = (int) Math.max(targetTicks - currentTicks, 0);
		int ticksToCrit = getTicksToCrit();

		return Math.max(ticksToAttack, ticksToCrit);
	}

	
	private int getTicksToCrit() {
		if (mc.player == null) {
			return 0;
		}
		if (shouldSkipCrit() || isCrit()) {
			return 0;
		}

		Vec3d prevPos = mc.player.getPos();

		for (int tick = 1; tick < 20; tick++) {
			Vec3d pos = Managers.PREDICT.extrapolate(mc.player, tick).getPos();

			if (prevPos.y > pos.y) {
				return tick;
			}

			prevPos = pos;
		}

		return 0;
	}

	private boolean isCrit() {
		if (mc.player == null) {
			return false;
		}

		return mc.player.fallDistance > 0.0f
				&& !mc.player.isOnGround()
				&& !mc.player.isClimbing()
				&& !mc.player.isTouchingWater()
				&& !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
				&& !mc.player.hasVehicle()
				&& !isCritSprinting();
	}

	private boolean isCritSprinting() {
		if (mc.player == null) {
			return false;
		}
		if (!config.getStopSprint().get()) {
			return false;
		}
		return mc.player.isSprinting();
	}

	private boolean shouldSkipCrit() {
		if (mc.player == null) {
			return false;
		}

		if (!config.getSmartCrit().get()) {
			return true;
		}

		return mc.player.getAbilities().flying
				|| (mc.player.isOnGround() && Managers.PLAYER.getOnGroundTicks() >= 3 && !config.getOnlyCrit().get())
				|| mc.player.isGliding()
				|| mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
				|| mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
				|| mc.player.isSubmergedInWater()
				|| mc.player.hasVehicle()
				|| mc.player.isClimbing()
				|| PlayerUtils.isInWeb();
	}
}
