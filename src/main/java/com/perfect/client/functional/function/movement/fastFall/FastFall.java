package com.perfect.client.functional.function.movement.fastFall;

import com.perfect.client.core.event.events.movement.MovementIterationEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.text.StringUtils;
import lombok.Getter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

@Getter

public class FastFall extends Function {

	private final FastFallConfig config = new FastFallConfig(this);

	public FastFall() {
		super("fast-fall", "Fast Fall", Category.MOVEMENT);
	}

	@Override
	public String getStatus() {
		return StringUtils.enumToFormattedString(config.getMode().get());
	}

	@Override
	public void onMovementIteration(MovementIterationEvent event) {
		if (mc.player == null) {
			return;
		}

		if (config.getMode().get() == Mode.TICK_SHIFT && shouldFall()) {
			event.cancel();
			event.setIterations(config.getPackets().get());
		}
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || !config.getMode().is(Mode.MOTION)) {
			return;
		}

		if (!shouldFall()) {
			return;
		}

		mc.player.setVelocity(
				mc.player.getVelocity().x * 0.05,
				getYVelocity(),
				mc.player.getVelocity().z * 0.05
		);
	}

	private double getYVelocity() {
		if (mc.player == null) {
			return 0f;
		}
		if (config.getAccelerate().get()) {
			return mc.player.getVelocity().y - config.getAccelerateSpeed().get();
		}
		return -config.getSpeed().get();
	}

	private boolean shouldFall() {
		if (mc.player == null || mc.world == null) {
			return false;
		}

		if (config.getPauseOnLagBack().get() && !Managers.ANTI_CHEAT.passed(config.getLagBackTime().get())) {
			return false;
		}

		if (mc.player.isTouchingWater()
				|| mc.player.isSubmergedInWater()
				|| mc.player.isHoldingOntoLadder()
				|| mc.player.isInLava()
				|| mc.player.isGliding()
				|| mc.player.noClip
				|| mc.player.getAbilities().flying
				|| PlayerUtils.isInWeb()
				|| mc.player.isRiding()
				|| mc.player.input.playerInput.jump()
				|| mc.player.input.playerInput.sneak()
		) {
			return false;
		}

		if (!mc.player.isOnGround()) {
			return false;
		}

		if (config.getOnlyHole().get() && !isHole()) {
			return false;
		}

		return canFall();
	}

	private boolean canFall() {
		if (mc.player == null || mc.world == null) {
			return false;
		}

		Box bb = mc.player.getBoundingBox();

		if (!mc.world.isSpaceEmpty(mc.player, bb.stretch(0, -0.01, 0))) {
			return false;
		}

		for (double i = 0.02; i < config.getHeight().get() + 0.03; i += 0.01) {
			if (!mc.world.isSpaceEmpty(mc.player, bb.offset(0.0, -i, 0.0))) {
				return true;
			}
		}

		return false;
	}

	public boolean isHole() {
		if (mc.player == null) {
			return false;
		}

		BlockPos playerPos = mc.player.getBlockPos();

		for (int i = 0; i < 10; i++) {
			if (BlockUtils.isCollidable(playerPos.down(i))) {
				return false;
			}

			if (HoleUtils.isInHole(playerPos.down(i))) {
				return true;
			}
		}

		return false;
	}

	public enum Mode {
		TICK_SHIFT,
		MOTION,
		NONE
	}
}
