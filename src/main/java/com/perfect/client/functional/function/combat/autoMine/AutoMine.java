package com.perfect.client.functional.function.combat.autoMine;

import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.functions.mining.RebreakMode;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.autoMine.data.PositionInfo;
import com.perfect.client.functional.function.combat.autoMine.helpers.Helper;
import com.perfect.client.functional.function.combat.autoMine.helpers.PositionHelper;
import com.perfect.client.functional.function.player.packetMine.data.ClientBreakInfo;
import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.core.utils.game.world.BlockUtils.isAir;
import static com.perfect.client.core.utils.game.world.BlockUtils.isReplaceable;
import static com.perfect.client.manager.system.FunctionManager.autoCrystal;
import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class AutoMine extends Function {

	@Getter
	private final AutoMineConfig config = new AutoMineConfig(this);

	private final PositionHelper positionHelper = new PositionHelper(this, config);

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	);

	@Getter
	private final Helper helper = new Helper(this, config);

	private BlockPos targetCrystalPos;

	private final TimerUtils pendingCrystalResetTimer = new TimerUtils();

	public AutoMine() {
		super("auto-mine", "Auto Mine", Category.COMBAT, "Auto City", "Cev Breaker");
	}

	@Override
	public void onEntitySpawn(EntityEvent event) {
		if (mc.player == null
				|| mc.player.networkHandler == null
				|| mc.world == null
				|| shouldPause()
				|| !config.getBreakCrystal().get()
				|| targetCrystalPos == null
				|| !(event.getEntity() instanceof EndCrystalEntity crystal)) {
			return;
		}

		attackCrystal(crystal);
	}

	@Override
	public void onAutoMineRotate() {
		if (shouldPause()) {
			return;
		}
		if (mc.player != null && targetCrystalPos != null && !config.getInstantRotate().get()) {
			Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(targetCrystalPos.toCenterPos().add(0, 0.5, 0)));
			Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(targetCrystalPos.toCenterPos().add(0, 0.5, 0)));
		}
	}

	@Override
	public void onPostTick() {
		if (mc.player == null || mc.world == null)
			return;
		if (shouldPause())
			return;

		if (pendingCrystalResetTimer.passedMillis(Managers.PING.getPing() + 200)) {
			targetCrystalPos = null;
		}

		breakCrystalTick();

		if (!packetMine.isEnabled() || isQueueFull()) {
			return;
		}

		boolean surrounded = TrapUtils.isTrapped(mc.player, TrapType.SURROUND_WITHOUT_FLOOR);

		boolean support = shouldSupport(surrounded);

		if (shouldWaitReBreak()) {
			PositionInfo rebreakPosition = positionHelper.getPositionByBlock(
					packetMine.getRebreakInfo().getPos(),
					support
			);

			placeSupport(rebreakPosition, true);

			if (rebreakPosition.found()) {
				return;
			}
		}

		BlockPos excluded = startBreaking(null, support);

		if (excluded != null && packetMine.canAddExtra(MiningPriority.AUTO_MINE)) {
			startBreaking(excluded, support);
		}
	}

	
	private BlockPos startBreaking(BlockPos excluded, boolean support) {
		PositionInfo position = positionHelper.getPosition(excluded, support);

		if (!position.found()) {
			return null;
		}

		BlockPos minePos = position.getMinePosition();

		if (support
				&& targetCrystalPos == null
				&& (!config.getPreBreak().get() || !isAir(minePos)
		)) {
			placeSupport(position, false);
		}

		if (shouldMine(position)) {
			packetMine.startBreaking(minePos, Direction.UP, MiningPriority.AUTO_MINE, getId());
		}

		return minePos;
	}

	private boolean isQueueFull() {
		if (!packetMine.isFull()) {
			return false;
		}

		for (ClientBreakInfo info : packetMine.getAll()) {
			if (info.getPriority() < MiningPriority.AUTO_MINE) {
				return false;
			}
		}

		return true;
	}

	private boolean shouldMine(PositionInfo position) {
		if (isReBreaking(position.getMinePosition())) {
			return false;
		}
		if (isReplaceable(position.getMinePosition()) && !config.getPreBreak().get()) {
			return false;
		}
		return !packetMine.isMining(position.getMinePosition());
	}

	private boolean shouldWaitReBreak() {
		return config.getWaitRebreak().get()
				&& packetMine.getRebreakInfo() != null
				&& packetMine.getRebreak() != RebreakMode.NONE;
	}

	
	private void placeSupport(PositionInfo position, boolean waitPreBreak) {
		if (!position.found()) {
			return;
		}
		if (position.getSupportPositions().isEmpty()) {
			return;
		}

		SwapInfo swapInfo = null;

		for (BlockPos supportPos : position.getSupportPositions()) {
			if (waitPreBreak && supportPos.equals(position.getMinePosition())) {
				continue;
			}
			swapInfo = placeBlock(swapInfo, supportPos);
		}

		if (waitPreBreak) {
			boolean progressReached = isProgressReached(packetMine.getRebreak(), getBlockStrength(position));

			if (progressReached && position.getSupportPositions().contains(position.getMinePosition())) {
				BlockPos supportPos = position.getMinePosition();
				swapInfo = placeBlock(swapInfo, supportPos);
			}
		}

		if (swapInfo != null) {
			swapInfo.swapBack();
		}
	}

	
	private SwapInfo placeBlock(SwapInfo swapInfo, BlockPos supportPos) {
		if (!isReplaceable(supportPos) || Managers.PENDING.contains(supportPos)) {
			return swapInfo;
		}
		if (checkObstructiveCrystals(supportPos)) {
			return swapInfo;
		}

		if (swapInfo == null) {
			swapInfo = SwapUtils.swap(config.getSwap().get(), Items.OBSIDIAN);
		}

		InteractUtils.placeBlock(
				supportPos,
				Items.OBSIDIAN,
				Rotate.PACKET,
				swapInfo.getPlaceArgs()
		);

		if (config.getRender().get()) {
			renderer.add(supportPos);
		}

		return swapInfo;
	}

	private boolean checkObstructiveCrystals(BlockPos pos) {
		for (Entity entity : BlockUtils.getEntities(pos)) {
			if (entity instanceof EndCrystalEntity) {
				if (config.getBreakObstructive().get()) {
					targetCrystalPos = entity.getBlockPos().down();
					pendingCrystalResetTimer.reset();
				}
				return true;
			}
		}

		return false;
	}

	private float getBlockStrength(PositionInfo rebreakPosition) {
		if (mc.player == null) {
			return 0.0f;
		}

		BlockState state = Blocks.OBSIDIAN.getDefaultState();

		return packetMine.getBlockStrength(rebreakPosition.getMinePosition(), state, packetMine.getTool(state));
	}

	private boolean isProgressReached(RebreakMode mode, float blockStrength) {
		boolean isProgressReset = !config.getPreBreak().get();

		if (isProgressReset) {
			return true;
		}

		return switch (mode) {
			case INSTANT -> checkInstantRebreakProgress(blockStrength);
			case FAST -> checkFastRebreakProgress(blockStrength);
			default -> true;
		};
	}

	private boolean checkInstantRebreakProgress(float blockStrength) {
		if (packetMine.isAirPreBreaking()) {
			return checkFastRebreakProgress(blockStrength);
		}
		return packetMine.getRebreakTicks() > 8;
	}

	private boolean checkFastRebreakProgress(float blockStrength) {
		float progress = packetMine.getFastRebreakProgress(blockStrength, 1);
		return progress >= config.getRebreakProgress().fromPercent();
	}

	private boolean isReBreaking(BlockPos pos) {
		if (packetMine.getRebreak() == RebreakMode.NONE)
			return false;
		if (packetMine.getRebreakInfo() == null)
			return false;

		return packetMine.getRebreakInfo().getPos().equals(pos);
	}

	private void breakCrystalTick() {
		if (mc.player == null || mc.world == null)
			return;
		if (targetCrystalPos == null || !config.getBreakCrystal().get())
			return;

		for (Entity entity : WorldUtils.getEntities(new Box(targetCrystalPos.up()))) {
			if (!(entity instanceof EndCrystalEntity crystal))
				return;
			attackCrystal(crystal);
		}
	}

	
	private void attackCrystal(EndCrystalEntity crystal) {
		if (!crystal.getBlockPos().equals(targetCrystalPos.up()) || mc.player == null)
			return;

		targetCrystalPos = null;

		if (config.getInstantRotate().get()) {
			Vec3d vec3d = PositionMath.closestVec3d(mc.player.getEyePos(), crystal.getBoundingBox().expand(-0.1));
			sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
					RotationUtils.getYaw(vec3d),
					RotationUtils.getPitch(vec3d),
					mc.player.isOnGround(),
					mc.player.horizontalCollision
			));
		}

		sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
		sendPacket(new HandSwingC2SPacket(
				mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND));

		Managers.CRYSTAL.onAttackCrystal(crystal);
	}

	
	public void placeCrystal(BlockPos minePos) {
		if (mc.player == null || mc.player.networkHandler == null || mc.world == null) {
			return;
		}
		if (!config.getPlaceCrystal().get()) {
			return;
		}

		placeCrystal(positionHelper.getPositionByBlock(new BlockPos(minePos), false));
	}

	
	public void placeCrystal(PositionInfo position) {
		if (mc.getNetworkHandler() == null || mc.player == null || mc.world == null) {
			return;
		}
		if (!config.getPlaceCrystal().get()) {
			return;
		}
		if (!position.found()) {
			return;
		}

		BlockPos pos = position.getCrystalPosition();
		BlockHitResult result = new BlockHitResult(pos.toCenterPos().add(0, 0.5, 0), Direction.UP, pos, false);

		if (!config.getBreakOnlyTrapCev().get() || Helper.isCev(position.getType())) {
			pendingCrystalResetTimer.reset();
			targetCrystalPos = position.getCrystalPosition();
		}

		if (config.getInstantRotate().get()) {
			sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
					RotationUtils.getYaw(result.getPos()),
					RotationUtils.getPitch(result.getPos()),
					mc.player.isOnGround(),
					mc.player.horizontalCollision
			));
		}

		SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), Items.END_CRYSTAL);
		InteractUtils.placeBlockIternal(
				result,
				null,
				swapInfo.getPlaceArgs()
		);
		swapInfo.swapBack();

		autoCrystal.render(pos);
	}

	private boolean shouldSupport(boolean surrounded) {
		if (!config.getCrystalSupport().get())
			return false;
		if (!config.getSupportOnlyInHole().get()) {
			return true;
		}
		return surrounded;
	}

	private boolean shouldPause() {
		return PlayerUtils.shouldPause(config.getPauseOnEat().get(), config.getPauseOnBlink().get());
	}

	public enum SortProperty {
		DAMAGE,
		PRIORITY
	}
}