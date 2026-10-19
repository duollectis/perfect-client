package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.core.utils.range.IntRange;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PlayerManager extends Manager {

	private float playerSpeed;
	private int sprintDelay;
	private int onGroundTicks;

	@Setter
	private boolean skipJump;

	@Override
	public void onPostTick() {
		if (mc.player == null) {
			return;
		}

		playerSpeed = (float) Math.hypot(mc.player.getX() - mc.player.lastX, mc.player.getZ() - mc.player.lastZ);
		sprintDelay--;

		if (mc.player.isOnGround()) {
			onGroundTicks++;
		}
		else {
			onGroundTicks = 0;
		}
	}

	@Override
	public void onPreJump(Cancellable event) {
		if (mc.player == null)
			return;

		if (skipJump) {
			event.cancel();
			mc.player.jumpingCooldown = 0;
		}

		skipJump = false;
	}

	@Override
	public void onPostMovementInput(MovementInputEvent event) {
		if (mc.player == null) {
			return;
		}

		if (sprintDelay > 0) {
			mc.player.setSprinting(false);
			event.sprinting.set(false);
		}
	}

	public void setSprintDelay(IntRange range) {
		setSprintDelay(SecureRandomUtils.random(range));
	}

	public void setSprintDelay(int delay) {
		sprintDelay = Math.max(sprintDelay, delay);
	}
}
