package com.perfect.client.functional.function.movement.safeWalk.modes;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.utils.functions.delay.TickDelayUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalk;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalkConfig;
import com.perfect.client.functional.function.movement.safeWalk.utils.SafeWalkMode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.ArrayList;


public class LegitMode extends SafeWalkMode {

	private static final double FALL_OFFSET = 0.1d;
	private static final double SAFE_OFFSET = 0.25d;

	private final TickDelayUtils sneakDelay = new TickDelayUtils(() -> config.getDelay().get());

	public LegitMode(SafeWalk function, SafeWalkConfig config) {
		super(function, config);
	}

	@Override
	public void onEnable() {
		sneakDelay.setPassed();
	}

	
	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (!sneakDelay.isPassed()) {
			event.sneaking.set(true);
		}

		if (isUnsafe()) {
			event.sneaking.set(true);

			updateSneakDelay();
			sneakDelay.resetTimer();
		}
		else {
			sneakDelay.tick();
		}
	}

	@Override
	public void updateSneakDelay() {
		sneakDelay.update();
	}

	
	private boolean isUnsafe() {
		if (mc.player == null) {
			return false;
		}
		if (!mc.player.isOnGround()) {
			return false;
		}

		Box box = mc.player.getBoundingBox().offset(0, -FALL_OFFSET, 0).expand(-SAFE_OFFSET, 0, -SAFE_OFFSET);
		return !hasCollision(box);
	}

	private boolean hasCollision(Box box) {
		for (BlockPos pos : PositionMath.getBoxIntersectedMinimum(new ArrayList<>(), box)) {
			if (BlockUtils.isCollidable(pos)) {
				return true;
			}
		}
		return false;
	}
}
