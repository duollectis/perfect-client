package com.perfect.client.functional.function.player.infiniteElytra;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.player.infiniteElytra.utils.TrackerHandler;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.Vec3d;

// Todo: добавить сайлент мод для грима который будет сбрасывать скорость


public class InfiniteElytra extends Function {

	private final InfiniteElytraConfig config = new InfiniteElytraConfig(this);

	private final TrackerHandler trackerHandler = addHandler(new TrackerHandler(this, config));

	private final TimerUtils timer = new TimerUtils();

	private boolean switchBack;

	public InfiniteElytra() {
		super("infinite-elytra", "Infinite Elytra", Category.PLAYER);
	}

	@Override
	protected void onDisable() {
		reset();
	}

	@Override
	protected void onEnable() {
		reset();
	}

	
	@Override
	public void onPreTick() {
		if (mc.player == null) {
			return;
		}

		if (!PlayerUtils.canOpenElytra(false)) {
			reset();
			return;
		}

		if (switchBack) {
			switchBack = false;
			switchFallFlying();
			return;
		}

		if (extrapolateWater(config.getExtrapolation().get())) {
			reset();
			return;
		}

		if (trackerHandler.isFallFlying() || trackerHandler.shouldSkipFlyingSwitch()) {

			if (Managers.TICKRATE.getTimeSinceLastTick() >= 1100) {
				return;
			}

			if (checkCeiling() && timer.passedMillisTps(config.getSwitchDelay().get())) {
				timer.reset();
				switchFallFlying();
				switchBack = true;
				trackerHandler.waitPacket();
			}
		}
		else {
			reset();
		}
	}

	private boolean checkCeiling() {
		if (config.getPauseUnderCeiling().get()) {
			return checkStandingHitbox(config.getExtrapolation().get());
		}

		return true;
	}

	private void reset() {
		switchBack = false;
		timer.reset();
		trackerHandler.reset();
	}

	private static boolean extrapolateWater(int extrapolate) {
		assert mc.player != null;

		EntityPose pose = mc.player.isSneaking() ? EntityPose.CROUCHING : EntityPose.STANDING;
		EntityDimensions dimensions = mc.player.getDimensions(pose);
		Vec3d playerPos = mc.player.getPos();

		return EntityUtils.calculateFluidCollision(
				FluidTags.WATER,
				dimensions.getBoxAt(playerPos.add(EntityUtils.getVelocity(mc.player).multiply(extrapolate)))
		);
	}

	private static boolean checkStandingHitbox(int extrapolate) {
		assert mc.world != null;
		assert mc.player != null;

		EntityPose pose = mc.player.isSneaking() ? EntityPose.CROUCHING : EntityPose.STANDING;
		EntityDimensions dimensions = mc.player.getDimensions(pose);
		Vec3d playerPos = mc.player.getPos();

		if (mc.world.isSpaceEmpty(mc.player, dimensions.getBoxAt(playerPos.add(0, 0.5, 0)))) {
			Vec3d extrapolated = playerPos.add(EntityUtils.getVelocity(mc.player).multiply(extrapolate));
			return mc.world.isSpaceEmpty(mc.player, dimensions.getBoxAt(extrapolated));
		}

		return false;
	}

	private static void switchFallFlying() {
		assert mc.player != null;
		sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
	}
}
