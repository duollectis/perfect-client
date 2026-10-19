package com.perfect.client.functional.function.combat.anchorAura;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.function.PacketMineEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.PlaceRenderer;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import com.perfect.client.functional.function.combat.anchorAura.helpers.PositionHelper;
import com.perfect.client.manager.game.targetManager.ITarget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.*;

import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class AnchorAura extends Function implements ITarget {

	@Getter
	private final AnchorAuraConfig config = new AnchorAuraConfig(this);
	private final PositionHelper positionHelper = new PositionHelper(this, config);

	public final PlaceRenderer renderer = new PlaceRenderer(
			() -> config.getLineColor().get(),
			() -> config.getSideColor().get(),
			() -> config.getCosmetics().get().bundle()
	);

	public List<LivingEntity> targets;
	public PlaceInfo attackInfo;

	private Sequential autoSequential;

	private int placeTickTimer;
	private int breakTickTimer;

	public HashMap<BlockPos, StateInfo> states;

	private TimerUtils timer;

	private LivingEntity renderTarget;

	public AnchorAura() {
		super("anchor-aura", "Anchor Aura", Category.COMBAT);
	}

	@Override
	public void onEnable() {
		targets = new ArrayList<>();

		placeTickTimer = config.getPlaceDelay().get();
		breakTickTimer = config.getBreakDelay().get();

		timer = new TimerUtils();
		states = new HashMap<>();
	}

	@Override
	public void onDisable() {
		targets = null;
		attackInfo = null;

		states = null;
		timer = null;
		renderTarget = null;
	}

	@Override
	public void onPostTick() {
		action();
	}

	@Override
	public void onBlockUpdate(BlockUpdateEvent event) {
		if (event.getNewState().isOf(Blocks.RESPAWN_ANCHOR) || event.isCombatBreak()) {
			action();
		}
	}

	@Override
	public void onPacketMine(PacketMineEvent event) {
		states.put(event.getPos(), new StateInfo(Blocks.AIR.getDefaultState(), timer.getMillis()));
		action();
	}

	private void action() {
		placeTickTimer++;
		breakTickTimer++;

		if (mc.player == null || mc.world == null) {
			renderTarget = null;
			return;
		}

		updateStates(Managers.PING.getPing() + 100);

		FindItemResult anchor = InvUtils.find(Items.RESPAWN_ANCHOR);
		FindItemResult glowStone = InvUtils.find(Items.GLOWSTONE);

		if (!isItemFound(anchor) || !isItemFound(glowStone)) {
			renderTarget = null;
			return;
		}

		targets =
				TargetUtils.getTargetsInRange(
						config.getTargetRange().get(),
						true,
						entity -> config.getTargets().contains(entity.getType())
				);
		if (targets.isEmpty() || PlayerUtils.shouldPause(
				config.getPauseOnEat().get(),
				config.getPauseOnBlink().get()
		)) {
			renderTarget = null;
			attackInfo = null;
			return;
		}

		Managers.ASYNC.run(() -> {
			attackInfo = positionHelper.getPosition();
			if (attackInfo == null) {
				renderTarget = null;
				return;
			}

			renderTarget = attackInfo.getTarget();

			if (!attackInfo.getState().isReplaceable() && !attackInfo.getState().isOf(Blocks.RESPAWN_ANCHOR)) {
				if (packetMine.isEnabled()) {
					packetMine.startBreaking(
							attackInfo.getPos(),
							mc.player.getFacing().getOpposite(),
							MiningPriority.ANCHOR_AURA,
							getId()
					);
				}
				return;
			}

			if (config.getSequential().get() == Sequential.AUTO)
				autoSequential = getAutoSequential(attackInfo);
			else
				autoSequential = null;

			BlockHitResult
					interactResult =
					!attackInfo.getHitResult().getBlockPos().equals(attackInfo.getPos())
					? HitResultUtils.getInteractResult(attackInfo.getPos()) : attackInfo.getHitResult();

			if (attackInfo.getState().isReplaceable() && placeTickTimer > config.getPlaceDelay().get()) {
				placeAnchor(attackInfo.getPos(), attackInfo.getHitResult(), anchor);

				if (shouldSequentialBreak()) {
					chargeAnchor(interactResult, glowStone);
					destroyAnchor(interactResult);
					antiProtect(attackInfo, interactResult);
					breakTickTimer = 0;
				}

				placeTickTimer = 0;
			}
			else if (attackInfo.getState().isOf(Blocks.RESPAWN_ANCHOR) && breakTickTimer > config
					.getBreakDelay()
					.get()) {
				if (attackInfo.getState().get(Properties.CHARGES) < 1)
					chargeAnchor(interactResult, glowStone);
				destroyAnchor(interactResult);
				antiProtect(attackInfo, interactResult);
				breakTickTimer = 0;

				if (shouldSequentialPlace()) {
					placeAnchor(attackInfo.getPos(), attackInfo.getHitResult(), anchor);
					placeTickTimer = 0;
				}
			}
		});
	}

	private void chargeAnchor(BlockHitResult interactResult, FindItemResult glowStone) {
		if (mc.player == null || mc.world == null)
			return;
		SwapInfo glowStoneSwapInfo = SwapUtils.swap(config.getSwap().get(), glowStone);
		InteractUtils.placeBlockIternal(
				interactResult,
				glowStone,
				InteractUtils.Argument.NO_ITEM,
				mc.player.getOffHandStack().isOf(Items.GLOWSTONE) ? InteractUtils.Argument.OFF_HAND : null
		);
		glowStoneSwapInfo.swapBack();

		states.put(
				interactResult.getBlockPos(),
				new StateInfo(Blocks.RESPAWN_ANCHOR.getDefaultState().with(Properties.CHARGES, 1), timer.getMillis())
		);
	}

	private void destroyAnchor(BlockHitResult interactResult) {
		if (mc.player == null || mc.world == null)
			return;
		FindItemResult destroyResult = InvUtils.find(stack -> !stack.isOf(Items.GLOWSTONE), 0, 8);
		if (!destroyResult.found())
			destroyResult = new FindItemResult(ItemStack.EMPTY, mc.player.getInventory().getSelectedSlot());
		InteractUtils.placeBlockIternal(interactResult, destroyResult);
	}

	private void placeAnchor(BlockPos pos, BlockHitResult placeResult, FindItemResult anchor) {
		if (mc.player == null || mc.world == null)
			return;

		if (packetMine.isEnabled()) {
			packetMine.removePos(pos);
		}

		SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), anchor);
		InteractUtils.placeBlockIternal(
				placeResult,
				anchor,
				InteractUtils.Argument.NO_ITEM,
				mc.player.getOffHandStack().isOf(Items.RESPAWN_ANCHOR) ? InteractUtils.Argument.OFF_HAND : null
		);
		swapInfo.swapBack();

		states.put(pos, new StateInfo(Blocks.RESPAWN_ANCHOR.getDefaultState(), timer.getMillis()));
		if (config.getRender().get()) {
			renderer.add(pos);
		}
	}

	private void antiProtect(PlaceInfo info, BlockHitResult hitResult) {
		if (!config.getAntiProtect().get() || mc.player == null || mc.world == null)
			return;
		if (autoSequential != Sequential.PLACE && getAutoSequential(info) != Sequential.PLACE)
			return;

		List<BlockPos> trap = TrapUtils.getTrap(info.getTarget(), TrapType.SURROUND);
		trap.addAll(TrapUtils.getTrap(info.getTarget(), TrapType.HEAD));

		boolean skip = false;
		for (BlockPos blockPos : trap) {
			if (!BlockUtils.isBlastResist(blockPos) && !blockPos.equals(info.getPos())) {
				skip = true;
				break;
			}
		}
		if (skip)
			return;

		FindItemResult block = InvUtils.find(stack -> stack.getItem() instanceof BlockItem item && config
				.getAntiProtectBlock()
				.get()
				.contains(item.getBlock()));

		if (!block.found() || !isItemFound(block))
			return;

		SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), block);
		InteractUtils.placeBlockIternal(
				hitResult,
				block,
				InteractUtils.Argument.NO_ITEM,
				mc.player.getOffHandStack().isOf(block.stack().getItem()) ? InteractUtils.Argument.OFF_HAND : null
		);
		swapInfo.swapBack();

		if (block.stack().getItem() instanceof BlockItem item)
			states.put(info.getPos(), new StateInfo(item.getBlock().getDefaultState(), timer.getMillis()));
	}

	private boolean isItemFound(FindItemResult result) {
		return result.found() && config.getSwap().get().isSlotNormal(result.slot());
	}

	private boolean shouldSequentialPlace() {
		return config.getSequential().get() == Sequential.PLACE || config.getSequential().get() == Sequential.FULL
				|| autoSequential == Sequential.PLACE;
	}

	private boolean shouldSequentialBreak() {
		return config.getSequential().get() == Sequential.BREAK
				|| config.getSequential().get() == Sequential.FULL
				|| autoSequential == Sequential.BREAK;
	}

	private Sequential getAutoSequential(PlaceInfo info) {
		BlockPos placePos = info.getPos();
		List<BlockPos> surround = TrapUtils.getTrap(info.getTarget(), TrapType.SURROUND);

		if (!info.isInTrap())
			return Sequential.BREAK;

		boolean skip = false;
		for (BlockPos blockPos : surround) {
			if (!BlockUtils.isCollidable(blockPos) && !placePos.equals(blockPos)) {
				skip = true;
				break;
			}
		}
		if (!skip)
			return Sequential.PLACE;

		List<BlockPos> trap = TrapUtils.getTrap(info.getTarget(), TrapType.TRAP);

		skip = false;
		for (BlockPos blockPos : trap) {
			if (!BlockUtils.isCollidable(blockPos) && !placePos.equals(blockPos) && blockPos.getY() <= BlockPos
					.ofFloored(info.getTarget().getEyePos())
					.getY()) {
				skip = true;
				break;
			}
		}
		if (!skip)
			return Sequential.PLACE;
		return Sequential.BREAK;
	}

	private void updateStates(int millis) {
		if (states == null || states.isEmpty())
			return;

		try {
			for (Map.Entry<BlockPos, StateInfo> entry : new HashMap<>(states).entrySet()) {
				if ((timer.getMillis() - entry.getValue().time()) > millis)
					states.remove(entry.getKey());
			}
		}
		catch (Exception ignored) {
		}
	}

	@Override
	public LivingEntity getTarget() {
		return renderTarget;
	}

	@Override
	public String getStatus() {
		// [Artik, 10.5m]

		if (renderTarget == null || mc.world == null || mc.player == null) {
			return "None, 0.0m";
		}

		String targetName = renderTarget.getType().getName().getString();
		if (renderTarget instanceof PlayerEntity player) {
			targetName = player.getGameProfile().getName();
		}

		return String.format(
				Locale.US, "%s, %.1fm",
				targetName,
				mc.player.distanceTo(renderTarget)
		);
	}

	@Getter
	@Setter
	public static class PlaceInfo {

		private BlockPos pos;

		private BlockHitResult hitResult;
		private LivingEntity target;
		private boolean placing;
		private boolean inTrap;
		private BlockState state;

		public PlaceInfo(BlockPos pos) {
			this.pos = pos;
		}

	}

	public record StateInfo(
			BlockState state,
			long time
	) {}

	public enum Sequential {
		NONE,
		AUTO,
		PLACE,
		BREAK,
		FULL
	}
}
