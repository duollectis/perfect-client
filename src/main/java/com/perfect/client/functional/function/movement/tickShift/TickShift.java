package com.perfect.client.functional.function.movement.tickShift;

import com.perfect.client.core.event.events.movement.MovementIterationEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.functional.function.movement.tickShift.helpers.TimerCharge;
import lombok.Getter;

import static com.perfect.client.manager.system.FunctionManager.fakeLag;


public class TickShift extends Function {

	@Getter
	private final TickShiftConfig config = new TickShiftConfig(this);

	@Getter
	private final TimerCharge charge = new TimerCharge(this, config);

	private boolean isShifting;
	private int blinkTicks;

	public TickShift() {
		super("tick-shift", "Tick Shift", Category.MOVEMENT, "Timer");
	}

	@Override
	public void onEnable() {
		blinkTicks = 0;
		isShifting = false;

		if (isInfinite()) {
			charge.setFullCharge();
		}
	}

	public boolean isInfinite() {
		return config.getInfinite().get();
	}

	@Override
	public void onPreSendMovement() {
		if (config.getUseBlink().get() && !isInfinite()) {
			if (!PlayerUtils.isTickUpdateLock()) {
				blinkTicks++;
			}

			if (blinkTicks >= config.getBlinkTicks().get()) {
				fakeLag.disable();
			}
		}
	}

	@Override
	public void onMovementIteration(MovementIterationEvent event) {
		if (charge.getValue() < 1 || PlayerUtils.getSpeedInBPS().horizontalLength() <= 0) {
			return;
		}

		if (!isShifting && !isCharged()) {
			return;
		}

		if (config.getUseBlink().get() && !isInfinite()) {
			if (!fakeLag.isEnabled()) {
				blinkTicks = 0;
				fakeLag.enable(false);
			}
		}

		isShifting = true;

		if (!PlayerUtils.isTickUpdateLock()) {
			event.cancel();
			event.setIterations(Math.min(config.getPacketsInTick().get(), (int) charge.getValue()));
		}
	}

	@Override
	public void onPostSendMovement() {
		if ((charge.getValue() == 0 || PlayerUtils.getSpeedInBPS().horizontalLength() <= 0)
				&& isShifting
				&& !isInfinite()) {
			endShifting();
		}
	}

	private boolean isCharged() {
		if (isInfinite()) {
			return true;
		}

		return Math.round(charge.getValue() / config.getMaxPackets().get()) >= config.getMinCharge().fromPercent();
	}

	private void endShifting() {
		if (config.getUseBlink().get()) {
			fakeLag.disable();
		}

		isShifting = false;
	}
}
