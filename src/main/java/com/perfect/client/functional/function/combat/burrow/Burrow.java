package com.perfect.client.functional.function.combat.burrow;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.functions.PlaceUtils;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;


public class Burrow extends Function {

	private final TimerUtils timer = new TimerUtils();
	private BlockPos prevBlockPos;

	private final BurrowConfig config = new BurrowConfig(this);

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	);

	public Burrow() {
		super("burrow", "Burrow", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			disable();
			return;
		}

		prevBlockPos = null;

		reset();
	}

	@Override
	public void onDisable() {
		prevBlockPos = null;
	}

	@Override
	public void onPostTick() {
		if (!updatePrevPos()) {
			return;
		}
		action();
	}

	private boolean updatePrevPos() {
		if (mc.player == null) {
			return false;
		}

		if (prevBlockPos == null) {
			prevBlockPos = mc.player.getBlockPos();
		}

		if (!mc.player.getBlockPos().equals(prevBlockPos)) {
			disable();
			return false;
		}

		prevBlockPos = mc.player.getBlockPos();
		return true;
	}

	
	private void action() {
		if (mc.player == null || mc.world == null || mc.interactionManager == null || !mc.player.isOnGround()) {
			return;
		}

		BlockPos burrowPos = getBurrowPos();

		if (burrowPos == null || !isEnoughSpace(burrowPos) || intersects(burrowPos)) {
			return;
		}

		if (!timer.passedMillis(config.getDelay().get())) {
			return;
		}

		BlockHitResult hitResult = HitResultUtils.getPlaceResult(burrowPos);

		if (hitResult == null) {
			return;
		}

		rotateIfEnabled(hitResult);

		SwapInfo swapInfo = PlaceUtils.getSwapInfo(config.getSwap(), config.getBlocks());

		jump();

		place(swapInfo, hitResult, burrowPos);

		lagBack();

		swapInfo.swapBack();

		reset();

		if (config.getDisable().get()) {
			disable();
		}
	}

	
	private void jump() {
		switch (config.getMode().get()) {
			case NORMAL -> {
				sendPosition(0.42);
				sendPosition(0.75);
				sendPosition(1.01);
				sendPosition(1.16);
			}
			case CC -> {
				sendPosition(0.4);
				sendPosition(0.75);
				sendPosition(1.01);
				sendPosition(0.99999992);
			}
		}
	}

	
	private void lagBack() {
		if (mc.player == null) {
			return;
		}

		switch (config.getMode().get()) {
			case NORMAL -> sendPosition(config.getVClip().get());
			case CC -> {
				sendPosition(1.15);

				sendPacket(new PlayerMoveC2SPacket.Full(
						mc.player.getX(),
						mc.player.getY(),
						mc.player.getZ(),
						mc.player.getYaw(),
						mc.player.getPitch(),
						true,
						mc.player.horizontalCollision
				));

				mc.player.setPos(mc.player.getX(), mc.player.getY(), mc.player.getZ());
			}
		}
	}

	
	private void place(SwapInfo swapInfo, BlockHitResult hitResult, BlockPos pos) {
		boolean placed = InteractUtils.placeBlock(
				hitResult,
				null, Rotate.NONE,
				swapInfo.getPlaceArgs()
		);

		if (placed && config.getRender().get()) {
			renderer.add(pos);
		}
	}

	private boolean isEnoughSpace(BlockPos pos) {
		int height = 3;

		if (config.getMode().get(Mode.NORMAL)) {
			height = (int) Math.ceil(config.getVClip().get());
		}

		for (int i = 0; i < height; i++) {
			if (!BlockUtils.isReplaceable(pos.up(i))) {
				return false;
			}
		}

		return true;
	}

	private boolean intersects(BlockPos pos) {
		return BlockUtils.hasEntity(pos, entity -> entity instanceof EndCrystalEntity || (entity instanceof PlayerEntity && entity != mc.player));
	}

	private void sendPosition(double yOffset) {
		if (mc.player == null) {
			return;
		}
		sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
				mc.player.getX(),
				mc.player.getY() + yOffset,
				mc.player.getZ(),
				true,
				mc.player.horizontalCollision
		));
	}

	private void rotateIfEnabled(BlockHitResult hitResult) {
		if (config.getRotate().get()) {
			float yaw = RotationUtils.getYaw(hitResult.getPos());
			float pitch = RotationUtils.getPitch(hitResult.getPos());

			sendRotation(yaw, pitch);
		}
	}

	private void sendRotation(float yaw, float pitch) {
		if (mc.player != null) {
			sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(yaw, pitch, true, mc.player.horizontalCollision));
		}
	}

	private BlockPos getBurrowPos() {
		if (mc.player == null) {
			return null;
		}
		return new BlockPos(mc.player.getBlockX(), (int) Math.round(mc.player.getY()), mc.player.getBlockZ());
	}

	private void reset() {
		timer.reset();
	}

	public enum Mode {
		NORMAL,
		CC
	}
}