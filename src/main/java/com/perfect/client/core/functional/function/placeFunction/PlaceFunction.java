package com.perfect.client.core.functional.function.placeFunction;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.functions.PlaceUtils;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.interact.SupportUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;

public abstract class PlaceFunction extends Function {

	@Getter
	private final boolean breakCrystalsFunction;

	@Getter
	private final boolean instantProtectFunction;

	protected final PlaceRenderer renderer;

	protected final TimerUtils placeTimer = new TimerUtils();
	protected final TimerUtils breakTimer = new TimerUtils();

	protected final List<PlaceInfo> places = new ArrayList<>();

	protected EndCrystalEntity crystalToBreak;

	protected int shiftTimer;

	protected int shiftedThisTick;

	public PlaceFunction(
			String id,
			String name,
			Category category,
			boolean breakCrystalsFunction,
			boolean instantProtectFunction,
			String... aliases
	) {
		super(id, name, category, aliases);

		this.breakCrystalsFunction = breakCrystalsFunction;
		this.instantProtectFunction = instantProtectFunction;

		renderer = new PlaceRenderer(
				() -> getConfig().getLineColor().get(),
				() -> getConfig().getSideColor().get(),
				() -> getConfig().getCosmetics().get().bundle()
		);
	}

	@Override
	public String getTypeId() {
		return "place-function";
	}

	@Override
	public void onEnable() {
		shiftTimer = getConfig().getShiftDelay().get();
	}

	@Override
	public void onDisable() {
		clear();
		crystalToBreak = null;
		shiftTimer = 0;
	}

	@Override
	public void onPostSendMovement() {
		if (!clientConfig.getStrictMode().isStrict()) {
			doPlace();
		}
		shiftedThisTick = 0;
	}

	@Override
	public void onHandleInput() {
		if (clientConfig.getStrictMode().isStrict()) {
			doPlace();
		}
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		deactivateCheck();

		if (mc.world == null || mc.player == null || !isEnabled()) {
			return;
		}

		PlaceFunctionConfig<?> config = getConfig();

		if (!config.getInstantBreak().get() || !instantProtectFunction)
			return;
		if (!(event.getPacket() instanceof EntitySpawnS2CPacket packet))
			return;
		if (packet.getEntityType() != EntityType.END_CRYSTAL)
			return;
		if (places.isEmpty())
			return;
		if (shouldPause())
			return;

		EndCrystalEntity crystal = new EndCrystalEntity(mc.world, 0, 0, 0);
		crystal.onSpawnPacket(packet);

		PlaceInfo protectInfo = getInstantProtectInfo(crystal);
		if (protectInfo == null) {
			return;
		}

		if (config.getBreakRotate().get() && shiftedThisTick < config.getShiftTicks().get()) {
			PlaceUtils.packetRotate(PositionMath.closestVec3d(
					protectInfo.getHitResult().getPos(),
					crystal.getBoundingBox()
			));
			shiftedThisTick++;
		}

		PlaceUtils.attackCrystal(crystal);

		if (config.getPlaceRotate().get() && shiftedThisTick < config.getShiftTicks().get()) {
			PlaceUtils.packetRotate(protectInfo.getHitResult().getPos());
			shiftedThisTick++;
		}

		SwapInfo swapInfo = PlaceUtils.getSwapInfo(config.getSwap(), config.getBlocks());
		if (!swapInfo.isFound()) {
			return;
		}

		place(protectInfo, null, swapInfo.getPlaceArgs());
		swapInfo.swapBack();
	}

	@Override
	public void onPlaceFunctionRotate() {
		deactivateCheck();
		clear();

		if (mc.player == null || mc.world == null || shouldPause() || !isEnabled()) {
			return;
		}

		EndCrystalEntity crystal = fillPositions();
		crystalToBreak = crystal;

		onRotate_();

		Vec3d rotateVec3d = null;
		PlaceInfo placeInfo = PlaceUtils.getNextPlaceInfo(places, crystal, null);

		if (placeInfo == null) {
			return;
		}

		if (crystal != null && getConfig().getBreakRotate().get()) {
			rotateVec3d = PositionMath.closestVec3d(placeInfo.getHitResult().getPos(), crystal.getBoundingBox());
		}
		else if (getConfig().getPlaceRotate().get()) {
			rotateVec3d = placeInfo.getHitResult().getPos();
		}

		if (rotateVec3d != null) {
			Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(rotateVec3d));
			Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(rotateVec3d));
		}
	}

	protected void onRotate_() { // TODO: переименовать
	}

	protected void clear() {
		places.clear();
	}

	
	protected EndCrystalEntity fillPositions() {
		assert mc.player != null;

		List<BlockPos> positions = getTrap();

		if (positions == null || positions.isEmpty()) {
			return null;
		}

		trapHook(positions);

		SupportUtils.addSupport(positions, getConfig().getSupport().get(), getConfig().getSupportLength().get());
		positions = new ArrayList<>(positions
				.stream()
				.filter(pos -> !Managers.PENDING.contains(pos) || BlockUtils.hasEntity(
						pos,
						entity -> entity instanceof EndCrystalEntity
				))
				.toList());

		EndCrystalEntity crystal = getCrystal(positions);

		for (BlockPos placePos : positions) {
			Vec3d bestClosest = mc.player.getEyePos();

			if (crystal != null && PositionMath.intersectsOrCollides(crystal.getBoundingBox(), new Box(placePos))) {
				bestClosest = crystal.getBoundingBox().getCenter();
			}

			BlockHitResult hitResult = HitResultUtils.getPlaceResult(placePos, bestClosest, null, null);

			if (hitResult == null || isOutOfRange(hitResult)) {
				continue;
			}

			places.add(new PlaceInfo(placePos, hitResult));
		}
		return crystal;
	}

	protected boolean isOutOfRange(BlockHitResult hitResult) {
		return PositionMath.isOutOfRange(hitResult.getPos(), getConfig().getRange().get());
	}

	
	protected void doPlace() {
		deactivateCheck();
		shiftTimer++;

		if (mc.player == null || mc.world == null || shouldPause() || !isEnabled()) {
			return;
		}

		if (!placeTimer.passedMillis(getConfig().getDelay().get())) {
			return;
		}

		boolean breakingCrystal = crystalToBreak != null && breakTimer.passedMillis(getConfig().getBreakDelay().get());

		if (breakingCrystal) {
			PlaceUtils.attackCrystal(crystalToBreak);
			breakTimer.reset();
		}

		if (places.isEmpty()) {
			return;
		}

		placeTimer.reset();

		PlaceInfo placeInfo = PlaceUtils.getNextPlaceInfo(places, crystalToBreak, null);

		if (placeInfo == null) {
			return;
		}

		if (breakingCrystal && shiftedThisTick < getConfig().getShiftTicks().get() && getConfig()
				.getPlaceRotate()
				.get()) {
			PlaceUtils.packetRotate(placeInfo.getHitResult().getPos());
			shiftedThisTick++;
		}

		PlaceFunctionConfig<?> placeFunctionConfig = getConfig();
		SwapInfo swapInfo = PlaceUtils.getSwapInfo(placeFunctionConfig.getSwap(), placeFunctionConfig.getBlocks());

		if (!swapInfo.isFound()) {
			if (getConfig().getResourcesToggle().get()) {
				error("No resource found");
				disable();
			}

			return;
		}

		InteractUtils.Argument[] placeArgs = swapInfo.getPlaceArgs();
		List<PlaceInfo> wasPlaced = new ArrayList<>();

		if (place(placeInfo, null, placeArgs))
			wasPlaced.add(placeInfo);

		if (getConfig().getShiftTicks().get() > 0 && shiftTimer > getConfig().getShiftDelay().get()) {
			boolean wasShifted = false;

			while (shiftedThisTick < getConfig().getShiftTicks().get()) {
				PlaceInfo info = PlaceUtils.getNextPlaceInfo(places, null, wasPlaced);
				if (info == null)
					break;

				if (BlockUtils.isReplaceable(info.getPosition())) {
					if (getConfig().getPlaceRotate().get()) {
						if (BlockUtils.hasEntity(info.getPosition(), entity -> entity instanceof EndCrystalEntity)) {
							wasPlaced.add(info);
							continue;
						}

						PlaceUtils.packetRotate(info.getHitResult().getPos());
					}

					wasShifted = true;

					if (place(info, swapInfo.getResult(), placeArgs)) {
						wasPlaced.add(info);
					}

					shiftedThisTick++;
				}
			}

			if (wasShifted) {
				shiftTimer = 0;
			}
		}

		swapInfo.swapBack();
	}

	private boolean place(PlaceInfo placeInfo, FindItemResult fir, InteractUtils.Argument... args) {
		boolean placed = InteractUtils.placeBlock(placeInfo.getHitResult(), fir, Rotate.NONE, args);

		if (placed && getConfig().getRender().get()) {
			renderer.add(placeInfo.getPosition());
		}

		return placed;
	}

	protected void deactivateCheck() {}

	protected void trapHook(List<BlockPos> positions) {}

	public boolean shouldPause() {
		if (mc.player == null || mc.world == null) {
			return true;
		}

		if (getConfig().getOnlyOnGround().get() && !mc.player.isOnGround()) {
			return true;
		}

		return PlayerUtils.shouldPause(getConfig().getPauseOnEat().get());
	}

	protected abstract List<BlockPos> getTrap();

	protected abstract EndCrystalEntity getCrystal(List<BlockPos> positions);

	protected abstract PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal);

	protected abstract PlaceFunctionConfig<?> getConfig();

	public boolean isPlaceEnabled() {
		return true;
	}
}
