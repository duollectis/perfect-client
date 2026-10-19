package com.perfect.client.functional.function.combat.tntAura;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.PlaceUtils;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.interact.SupportUtils;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.tntAura.data.TntInfo;
import lombok.Getter;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class TntAura extends PlaceFunction {

	private final TntAuraConfig config = new TntAuraConfig(this);

	private final TimerUtils tntTimer = new TimerUtils();

	private TntInfo tntInfo;
	private boolean rotated;

	private boolean pauseRender;

	private final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	).pauseSupplier(() -> pauseRender);

	public TntAura() {
		super("tnt-aura", "Tnt Aura", Category.COMBAT, true, false, "Auto Tnt");
	}

	@Override
	public void onDisable() {
		super.onDisable();
		pauseRender = false;
	}

	@Override
	public boolean isPlaceEnabled() {
		return !config.getTntTrap().get(TntTrap.NONE);
	}

	@Override
	protected void clear() {
		super.clear();

		tntInfo = null;
		rotated = false;
	}

	
	@Override
	protected void onRotate_() {
		rotated = false;
		pauseRender = false;

		if (mc.player == null || mc.world == null) {
			return;
		}
		if (tntInfo == null) {
			return;
		}
		if (!isTrapComplete()) {
			return;
		}

		if (!SwapUtils.find(config.getSwap().get(), Items.TNT).isFound()) {
			return;
		}
		if (!SwapUtils.find(config.getSwap().get(), Items.FLINT_AND_STEEL).isFound()) {
			return;
		}

		if (!isReplaceableOrTnt(tntInfo.getTntPosition()) && config.getBreakTrap().get()) {
			startBreakingBlock();
			return;
		}

		pauseRender = true;

		if (!config.getTntSpam().get() && hasTntEntity(tntInfo.getTntPosition())) {
			return;
		}

		if (!tntTimer.passedMillis(config.getTntDelay().get())) {
			return;
		}

		if (config.getPlaceRotate().get()) {
			Vec3d rotateVec3d = tntInfo.getTntPlaceResult().getPos();
			Managers.ROTATION.setRotate(rotateVec3d);
		}

		tntTimer.reset();
		rotated = true;
	}

	private boolean hasTntEntity(BlockPos pos) {
		return BlockUtils.hasEntity(pos, entity -> entity instanceof TntEntity);
	}

	private boolean isReplaceableOrTnt(BlockPos pos) {
		if (BlockUtils.isReplaceable(pos)) {
			return true;
		}
		return isTnt(pos);
	}

	private void startBreakingBlock() {
		packetMine.startBreaking(
				tntInfo.getTntPosition(),
				tntInfo.getTntInteractResult().getSide(),
				MiningPriority.TNT_AURA,
				getId()
		);
	}

	
	@Override
	protected void doPlace() {
		super.doPlace();

		if (!rotated) {
			return;
		}

		SwapInfo tntSwapInfo = SwapUtils.find(config.getSwap().get(), Items.TNT);
		SwapInfo flintSwapInfo = SwapUtils.find(config.getSwap().get(), Items.FLINT_AND_STEEL);

		if (!tntSwapInfo.isFound() || !flintSwapInfo.isFound()) {
			return;
		}

		packetMine.removePos(tntInfo.getTntPosition());

		boolean isTnt = isTnt(tntInfo.getTntPosition());

		if (!isTnt) {
			tntSwapInfo.swap();

			boolean placed = InteractUtils.placeBlock(
					tntInfo.getTntPlaceResult(),
					flintSwapInfo.getResult(),
					Rotate.NONE,
					flintSwapInfo.getPlaceArgs()
			);

			if (placed && config.getRender().get()) {
				renderer.add(tntInfo.getTntPosition());
			}
		}

		flintSwapInfo.swap();

		InteractUtils.placeBlockIternal(
				tntInfo.getTntInteractResult(),
				flintSwapInfo.getResult(),
				flintSwapInfo.getPlaceArgs()
		);

		if (!isTnt) {
			tntSwapInfo.swapBack();
		}
		else {
			flintSwapInfo.swapBack();
		}
	}

	private boolean isTrapComplete() {
		if (!tntInfo.getTrapPositions().isEmpty()) {
			return false;
		}
		return isTntTrapped(tntInfo.getTntPosition());
	}

	
	@Override
	protected EndCrystalEntity fillPositions() {
		tntInfo = getPosition();

		if (tntInfo == null) {
			return null;
		}

		EndCrystalEntity crystal = null;

		for (PlaceInfo positionInfo : tntInfo.getTrapPositions()) {
			BlockPos pos = positionInfo.getPosition();

			if (crystal == null) {
				crystal = PlaceUtils.getCrystalByBlock(pos);
			}
			if (!Managers.PENDING.contains(pos)) {
				places.add(positionInfo);
			}
		}

		if (crystal == null) {
			crystal = PlaceUtils.getCrystalByBlock(tntInfo.getTntPosition());
		}

		return crystal;
	}

	private TntInfo getPosition() {
		TntInfo bestInfo = null;

		for (LivingEntity player : getPlayersInRange()) {
			TntInfo position = getTntPositionByEntity(player);

			if (bestInfo != null && position.getStatePriority().getValue() < bestInfo.getStatePriority().getValue()) {
				continue;
			}
			if (bestInfo != null && position.getTrapSize() > bestInfo.getTrapSize()) {
				continue;
			}

			bestInfo = position;
		}

		return bestInfo;
	}

	public List<LivingEntity> getPlayersInRange() {
		return TargetUtils.getTargetsInRange(config.getRange().get() + 1, false, this::isGoodTarget);
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return false;
		}
		if (!config.getOnlyTargetsInHole().get()) {
			return true;
		}
		return TrapUtils.isTrapped(entity, TrapType.SURROUND_WITHOUT_FLOOR, BlockUtils::isCollidable);
	}

	
	private TntInfo getTntPositionByEntity(LivingEntity entity) {
		assert mc.player != null;

		TntInfo bestInfo = null;

		for (BlockPos headPos : EntityUtils.getIntersectedHead(entity)) {

			BlockPos tntPos = headPos.up();

			if (hasEntity(tntPos)) {
				continue;
			}

			boolean replaceable = BlockUtils.isReplaceable(tntPos) || isTnt(tntPos);

			StatePriority statePriority = getStatePriority(tntPos);

			if (bestInfo != null && statePriority.getValue() < bestInfo.getStatePriority().getValue()) {
				continue;
			}

			if (!config.getBreakTrap().get() && !replaceable) {
				continue;
			}
			if (!replaceable && !BlockUtils.isBreakable(tntPos)) {
				continue;
			}
			if (config.getTntTrap().get(TntTrap.NONE) && !isTntTrapped(tntPos)) {
				continue;
			}

			List<PlaceInfo> tntTrap = getTntTrap(entity, tntPos);

			if (tntTrap == null) {
				continue;
			}

			int trapSize = tntTrap.size();

			if (bestInfo != null && trapSize > bestInfo.getTrapSize()) {
				continue;
			}

			BlockHitResult tntPlaceResult = HitResultUtils.getPlaceResult(
					tntPos,
					mc.player.getEyePos(),
					toPosList(tntTrap),
					Utils.listOf(tntPos)
			);

			if (tntPlaceResult == null) {
				continue;
			}

			BlockHitResult tntInteractResult = HitResultUtils.getInteractResult(tntPos);

			if (tntInteractResult == null) {
				continue;
			}

			bestInfo = new TntInfo(tntPos, tntPlaceResult, tntInteractResult, tntTrap, statePriority, trapSize);
		}

		return bestInfo;
	}

	private boolean isTnt(BlockPos pos) {
		return BlockUtils.isOf(pos, Blocks.TNT);
	}

	private StatePriority getStatePriority(BlockPos pos) {
		if (BlockUtils.isReplaceable(pos)) {
			return StatePriority.REPLACEABLE;
		}
		if (packetMine.isMining(pos)) {
			return StatePriority.BLOCK_MINING;
		}
		return StatePriority.BLOCK;
	}

	private List<BlockPos> toPosList(List<PlaceInfo> list) {
		List<BlockPos> positions = new ArrayList<>();

		for (PlaceInfo info : list) {
			positions.add(info.getPosition());
		}

		return positions;
	}

	private List<PlaceInfo> getTntTrap(LivingEntity player, BlockPos tntPos) {
		List<BlockPos> trapBlocks = getTntTrapBlocksReplaceable(player, tntPos);

		SupportUtils.addSupport(
				trapBlocks,
				getConfig().getSupport().get(),
				getConfig().getSupportLength().get(),
				Utils.listOf(tntPos)
		);

		List<BlockPos> accepted = new ArrayList<>();
		List<PlaceInfo> trap = new ArrayList<>();

		for (BlockPos pos : trapBlocks) {
			if (hasEntity(pos)) {
				return null;
			}

			BlockHitResult hitResult = HitResultUtils.getPlaceResult(pos, accepted);

			if (hitResult == null || isOutOfRange(hitResult)) {
				return null;
			}

			accepted.add(pos);
			trap.add(new PlaceInfo(pos, hitResult));
		}

		return trap;
	}

	private boolean hasEntity(BlockPos pos) {
		return BlockUtils.hasEntity(pos, this::isObstructiveEntity);
	}

	private boolean isObstructiveEntity(Entity entity) {
		if (entity instanceof TntEntity) {
			return false;
		}
		if (entity instanceof EndCrystalEntity) {
			return !config.getBreakCrystals().get();
		}
		return entity.intersectionChecked;
	}

	private List<BlockPos> getTntTrapBlocksReplaceable(LivingEntity player, BlockPos tntPos) {
		List<BlockPos> tntTrap = getTntTrapBlocks(tntPos);

		if (config.getFaceTrap().get()) {
			List<BlockPos> faceTrap = TrapUtils.getTrap(player, TrapType.FACE);
			tntTrap.addAll(faceTrap);
		}

		return new ArrayList<>(tntTrap.stream().filter(
				pos -> BlockUtils.isReplaceable(pos) && !Managers.PENDING.contains(pos)
		).toList());
	}

	private List<BlockPos> getTntTrapBlocks(BlockPos tntPos) {
		return switch (config.getTntTrap().get()) {
			case NONE -> new ArrayList<>();
			case TOP -> getTopTntTrap(tntPos);
			case FULL -> getFullTntTrap(tntPos);
		};
	}

	private List<BlockPos> getTopTntTrap(BlockPos tntPos) {
		BlockPos trapPos = tntPos.up();
		return Utils.listOf(trapPos);
	}

	private List<BlockPos> getFullTntTrap(BlockPos tntPos) {
		List<BlockPos> trap = new ArrayList<>();

		for (Direction direction : Direction.values()) {
			if (direction.equals(Direction.DOWN)) {
				continue;
			}
			BlockPos trapPos = tntPos.offset(direction);
			trap.add(trapPos);
		}

		return trap;
	}

	private boolean isTntTrapped(BlockPos tntPos) {
		if (needTopTrap() && BlockUtils.isReplaceable(tntPos.up())) {
			return false;
		}

		for (Direction direction : Direction.Type.HORIZONTAL) {
			BlockPos trapPos = tntPos.offset(direction);

			if (BlockUtils.isReplaceable(trapPos)) {
				return false;
			}
		}
		return true;
	}

	private boolean needTopTrap() {
		if (!config.getTntTrap().get(TntTrap.NONE)) {
			return true;
		}
		return config.getOnlyTopTrapped().get();
	}

	@Override
	protected List<BlockPos> getTrap() {
		return List.of();
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		return null;
	}

	@Override
	protected PlaceFunctionConfig<?> getConfig() {
		return config;
	}

	public enum TntTrap {
		NONE,
		TOP,
		FULL
	}

	@Getter
	public enum StatePriority {
		REPLACEABLE(2),
		BLOCK_MINING(1),
		BLOCK(0);

		final int value;

		StatePriority(int value) {
			this.value = value;
		}
	}
}
