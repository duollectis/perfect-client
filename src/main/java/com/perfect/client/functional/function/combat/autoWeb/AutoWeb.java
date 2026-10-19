package com.perfect.client.functional.function.combat.autoWeb;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.autoWeb.data.PlaceInfo;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AutoWeb extends Function {

	private static final float HITBOX_TARGET_OFFSET = 0.07f;

	private final AutoWebConfig config = new AutoWebConfig(this);

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	);

	private PlaceInfo placeInfo;
	private SwapInfo swapInfo;

	private final TimerUtils timer = new TimerUtils();

	public AutoWeb() {
		super("auto-web", "Auto Web", Category.COMBAT);
	}

	private void reset() {
		placeInfo = null;
		swapInfo = SwapInfo.NOT_FOUND;
	}

	
	@Override
	public void onAutoWebRotate() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		if (!timer.passedMillis(config.getPlaceDelay().get()) || shouldPause()) {
			reset();
			return;
		}

		placeInfo = findPlaceInfo();
		swapInfo = findSwap();

		if (placeInfo == null || !swapInfo.isFound()) {
			reset();
			return;
		}

		Vec3d rotateVec3d = placeInfo.getHitResult().getPos();

		if (config.getRotate().get()) {
			Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(rotateVec3d));
			Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(rotateVec3d));
		}
	}

	
	@Override
	public void onPostSendMovement() {
		if (mc.player == null || mc.world == null) {
			return;
		}
		if (placeInfo == null || !swapInfo.isFound()) {
			reset();
			return;
		}

		swapInfo.swap();

		boolean placed = InteractUtils.placeBlock(
				placeInfo.getHitResult(),
				new FindItemResult(new ItemStack(Items.COBWEB), 0),
				Rotate.NONE,
				swapInfo.getPlaceArgs()
		);

		if (config.getRender().get() && placed) {
			renderer.add(placeInfo.getPos());
		}

		swapInfo.swapBack();
		timer.reset();
	}

	private SwapInfo findSwap() {
		return SwapUtils.find(config.getSwap().get(), Items.COBWEB);
	}

	private boolean shouldPause() {
		if (mc.player == null) {
			return false;
		}

		if (config.getOnlyOnGround().get() && !mc.player.isOnGround()) {
			return true;
		}

		return PlayerUtils.shouldPause(
				config.getPauseOnEat().get(),
				config.getPauseOnBlink().get(),
				config.getPauseOnAutoExp().get()
		);
	}

	private PlaceInfo findPlaceInfo() {
		List<BlockPos> excluded = getExcluded();
		return findPlaceInfo(excluded);
	}

	private PlaceInfo findPlaceInfo(List<BlockPos> excluded) {
		for (LivingEntity player : getTargets()) {
			PlaceInfo placeInfo = getPosByPlayer(excluded, player);

			if (placeInfo != null) {
				return placeInfo;
			}
		}
		return null;
	}

	
	private PlaceInfo getPosByPlayer(List<BlockPos> excluded, LivingEntity player) {
		for (BlockPos pos : getPlayerIntersectedBlocks(player)) {
			if (excluded.contains(pos)) {
				continue;
			}
			if (!canPlaceAt(pos)) {
				continue;
			}

			BlockHitResult hitResult = HitResultUtils.getPlaceResult(pos);

			if (hitResult != null) {
				return new PlaceInfo(pos, hitResult);
			}
		}
		return null;
	}

	private List<BlockPos> getPlayerIntersectedBlocks(LivingEntity player) {
		Box box = getPlayerIntersectionBox(player);
		return PositionMath.getIntersectedBlocks(box);
	}

	private Box getPlayerIntersectionBox(LivingEntity player) {
		Box bb = getPredictedBox(player).expand(-HITBOX_TARGET_OFFSET);

		return switch (config.getMode().get()) {
			case FEET -> new Box(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ);
			case HEAD -> new Box(bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
			case FULL -> bb;
		};
	}

	private List<BlockPos> getExcluded() {
		if (mc.player == null) {
			return Collections.emptyList();
		}
		List<BlockPos> excluded = new ArrayList<>();

		if (config.getNoSelfWeb().get()) {
			Box box = mc.player.getBoundingBox().expand(-HITBOX_TARGET_OFFSET);
			excluded.addAll(PositionMath.getIntersectedBlocks(box));
		}

		return excluded;
	}

	private boolean canPlaceAt(BlockPos pos) {
		if (!BlockUtils.isReplaceable(pos)) {
			return false;
		}
		if (BlockUtils.isOutOfBorder(pos)) {
			return false;
		}
		if (BlockUtils.hasEntity(pos, entity -> entity instanceof EndCrystalEntity)) {
			return false;
		}
		return !PositionMath.isOutOfRange(pos, config.getPlaceRange().get());
	}

	private Box getPredictedBox(LivingEntity player) {
		return Managers.PREDICT.extrapolate(player, config.getExtrapolation().get()).getBox();
	}

	private List<LivingEntity> getTargets() {
		return TargetUtils.getTargetsInRange(
				20.0,
				false,
				entity -> entity instanceof PlayerEntity
		);
	}

	public enum Mode {
		FEET,
		HEAD,
		FULL
	}
}
