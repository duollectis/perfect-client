package com.perfect.client.functional.function.movement.speed.modes;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.functions.MovementUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.movement.speed.Speed;
import com.perfect.client.functional.function.movement.speed.SpeedConfig;
import com.perfect.client.functional.function.movement.speed.utils.SpeedMode;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.tag.FluidTags;
import org.joml.Vector2d;

import static com.perfect.client.manager.system.FunctionManager.step;
import static com.perfect.client.manager.system.FunctionManager.tickShift;


public class StrafeMode extends SpeedMode {

	private final TimerUtils waterTimer = new TimerUtils();

	public int stage;
	protected double distance, playerSpeed;
	private long timer = 0L;

	private boolean wasSpeedEffect;
	private boolean wasSlownessEffect;

	public StrafeMode(Speed function, SpeedConfig config) {
		super(function, config);
	}

	@Override
	public void onEnable() {
		waterTimer.setMillis(200);
	}

	@Override
	public void onPreTick() {
		if (mc.player == null) {
			return;
		}

		distance = PlayerUtils.getVelocity().horizontalLength();
	}

	
	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		if (mc.world == null || mc.player == null)
			return;
		if (event.getType() != MovementType.SELF || mc.player.getAbilities().flying)
			return;

		if (tickShift.isEnabled() && tickShift.getCharge().getValue() > 0)
			stage = 0;

		if (wasSpeedEffect != mc.player.hasStatusEffect(StatusEffects.SPEED))
			stage = 0;
		else if (wasSlownessEffect != mc.player.hasStatusEffect(StatusEffects.SLOWNESS))
			stage = 0;

		wasSpeedEffect = mc.player.hasStatusEffect(StatusEffects.SPEED);
		wasSlownessEffect = mc.player.hasStatusEffect(StatusEffects.SLOWNESS);

		if (PlayerUtils.shouldPause(config.getPauseOnEat().get(), config.getPauseOnShift().get(), false, false)) {
			stage = 0;
			return;
		}
		if (config.getPauseOnElytra().get() && mc.player.isGliding()) {
			stage = 0;
			return;
		}
		if (config.getPauseInLiquid().get() && (mc.player.isTouchingWater() || mc.player.isInFluid()
				|| mc.player.isSubmergedIn(FluidTags.WATER) || mc.player.isSubmergedIn(FluidTags.LAVA)
		)) {
			stage = 0;
			waterTimer.reset();
			return;
		}
		if (!waterTimer.passedMillis(500)) {
			stage = 0;
			return;
		}

		switch (stage) {
			case 0:
				if (PlayerUtils.isInputMoving()) {
					++stage;
					playerSpeed = getDefaultSpeed() - 0.01;
				}
			case 1:
				break;
			case 2:
				playerSpeed = distance - 0.76 * (distance - getDefaultSpeed());
				++stage;
				break;
			case 3:
				if (!mc.world.isSpaceEmpty(mc.player.getBoundingBox().offset(0.0, mc.player.getVelocity().y, 0.0))
						|| mc.player.verticalCollision && stage > 0) {
					stage = 0;
				}
				playerSpeed = distance - distance / 159.0;
				break;
			case 4:
				stage = 2;
				break;
			default:
				--stage;
				return;
		}

		playerSpeed = Math.max(playerSpeed, getDefaultSpeed());

		if (config.getNcpStrict().get()) {
			if (System.currentTimeMillis() - timer > 2500L) {
				timer = System.currentTimeMillis();
			}

			playerSpeed = Math.min(playerSpeed, System.currentTimeMillis() - timer > 1250L ? 0.44 : 0.43);
		}

		Vector2d strafe = MovementUtils.forwardStrafe(playerSpeed);

		if (strafe == null) {
			return;
		}

		event.setX(strafe.x);
		event.setZ(strafe.y);
	}

	@Override
	public void onPreJump(Cancellable event) {
		if (mc.player == null) {
			return;
		}

		if (mc.player.horizontalCollision && step.isEnabled()) {
			event.setCancelled(true);
			return;
		}

		playerSpeed *= config.getNcpStrafeSpeed().get();
		stage = 4;
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.player == null) {
			return;
		}
		if (config.getNcpAutoJump().get()
				&& PlayerUtils.isInputMoving()
				&& mc.player.isOnGround()
				&& !mc.player.horizontalCollision) {
			event.jumping.set(true);
		}
	}

	@Override
	public void onItemSlowDown(Cancellable event) {
		if (!function.isPaused()) {
			event.cancel();
		}
	}

	private double getDefaultSpeed() {
		return config.getNcpGroundSpeed().fromBPS() * PlayerUtils.getSpeedMultiplier();
	}
}
