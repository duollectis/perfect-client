package com.perfect.client.functional.function.combat.blocker.modes.blind;

import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.blocker.Blocker;
import com.perfect.client.functional.function.combat.blocker.BlockerConfig;
import com.perfect.client.functional.function.combat.blocker.helpers.BlockerMode;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlindMode extends BlockerMode {

	private final HashMap<BlockPos, PendingBlock> pendingBlockMap = new HashMap<>();

	public BlindMode(
			Blocker function,
			BlockerConfig config
	) {
		super(function, config);
	}

	@Override
	public void onDisable() {
		pendingBlockMap.clear();
	}

	
	@Override
	public void addTrap(List<BlockPos> list) {
		if (mc.player == null) {
			return;
		}
		if (!TrapUtils.isTrapped(function.getSurround())) {
			return;
		}

		for (Map.Entry<BlockPos, PendingBlock> entry : pendingBlockMap.entrySet()) {
			BlockPos pos = entry.getKey();
			PendingBlock pendingBlock = entry.getValue();

			if (pendingBlock.getProgress() >= config.getBreakProgress().fromPercent()) {
				addBlockProtect(list, pendingBlock.getProtect(), pos);
			}
		}
	}

	
	private void addBlockProtect(List<BlockPos> list, Protect protect, BlockPos pos) {
		switch (protect) {
			case SURROUND -> function.addBlockProtect(list, Blocker.ProtectMode.SURROUND, pos);
			case SELF_TRAP -> function.addBlockProtect(list, Blocker.ProtectMode.SELF_TRAP, pos);
			case DOUBLE_SELF_TRAP -> function.addInList(list, pos.down());
		}
	}

	
	@Override
	public void onTick() {
		if (mc.world == null || mc.player == null) {
			return;
		}

		addPendingProtection();

		List<BlockPos> toRemove = new ArrayList<>();

		for (Map.Entry<BlockPos, PendingBlock> entry : pendingBlockMap.entrySet()) {
			BlockPos pos = entry.getKey();
			PendingBlock pendingBlock = entry.getValue();

			if (!isNormalPending(pos, pendingBlock)) {
				toRemove.add(pos);
				continue;
			}

			pendingBlock.addProgress(pos);
		}

		for (BlockPos removePos : toRemove) {
			pendingBlockMap.remove(removePos);
		}
	}

	
	private void addPendingProtection() {
		addPendingProtection(
				function.getSurround(),
				Protect.SURROUND,
				config.getSurroundPredict().get()
		);

		addPendingProtection(
				function.getSelfTrap(),
				Protect.SELF_TRAP,
				config.getSelfTrapPredict().get()
		);
	}

	
	private void addPendingProtection(
			List<BlockPos> trap,
			Protect protectMode,
			Blocker.BlindProtectMode predictMode
	) {
		if (mc.world == null) {
			return;
		}

		if (predictMode == Blocker.BlindProtectMode.NONE) {
			return;
		}

		for (BlockPos pos : trap) {
			if (pendingBlockMap.containsKey(pos)) {
				continue;
			}
			if (protectMode.isCev() && !BlockUtils.isOf(pos, Blocks.OBSIDIAN)) {
				continue;
			}

			for (PlayerEntity player : mc.world.getPlayers()) {
				if (needProtectAt(player, pos, trap, protectMode, predictMode)) {
					pendingBlockMap.put(pos, new PendingBlock(protectMode, player));
				}
			}
		}
	}

	
	private boolean needProtectAt(
			PlayerEntity player,
			BlockPos pos,
			List<BlockPos> trap,
			Protect protectMode,
			Blocker.BlindProtectMode predictMode
	) {
		if (!isEnemy(player, pos)) {
			return false;
		}

		return switch (predictMode) {
			case NONE -> false;
			case PREDICTIVE -> needProtectPredictive(player, pos, trap, protectMode);
			case DELAYED -> needProtectState(pos);
		};
	}

	
	private boolean needProtectState(BlockPos pos) {
		if (BlockUtils.isReplaceable(pos)) {
			return false;
		}
		return BlockUtils.isBreakable(pos);
	}

	
	private boolean needProtectPredictive(
			PlayerEntity enemy,
			BlockPos pos,
			List<BlockPos> trap,
			Protect protectMode
	) {
		BlockPos predictedPos = predictPos(enemy, trap, protectMode);
		return needProtectState(pos) && pos.equals(predictedPos);
	}

	
	private BlockPos predictPos(
			PlayerEntity enemy,
			List<BlockPos> trap,
			Protect protectMode
	) {
		switch (protectMode) {
			case SELF_TRAP -> {
				return predictSelfTrap(enemy, trap);
			}
			case SURROUND -> {
				return predictSurround(enemy, trap);
			}
		}
		return null;
	}

	
	private BlockPos predictSurround(
			PlayerEntity enemy,
			List<BlockPos> surround
	) {
		BlockPos bestPos = null;
		double maxDamage = -1.0f;

		for (BlockPos pos : getTrapTargetInRange(enemy, surround)) {

			double damage = getDamage(pos);

			if (damage > maxDamage) {
				maxDamage = damage;
				bestPos = pos;
			}
		}

		return bestPos;
	}

	
	private BlockPos predictSelfTrap(
			PlayerEntity enemy,
			List<BlockPos> selfTrap
	) {
		BlockPos bestPos = null;
		double maxDamage = -1.0f;

		for (BlockPos pos : getTrapTargetInRange(enemy, selfTrap)) {
			if (!canCevAt(pos)) {
				continue;
			}
			if (!needProtectState(pos)) {
				continue;
			}

			double damage = getDamage(pos);

			if (damage > maxDamage) {
				maxDamage = damage;
				bestPos = pos;
			}
		}

		return bestPos;
	}

	private boolean canCevAt(BlockPos pos) {
		return BlockUtils.isAir(pos.up());
	}

	private double getDamage(BlockPos pos) {
		if (mc.player == null) {
			return 0f;
		}

		return DamageCalculator.getExplosionDamage(
				mc.player,
				mc.player.getBoundingBox(),
				BlockUtils.getCrystalVec3d(pos),
				false,
				false,
				true,
				6,
				pos
		);
	}

	private List<BlockPos> getTrapTargetInRange(PlayerEntity enemy, List<BlockPos> trap) {
		List<BlockPos> trapInRange = new ArrayList<>();

		for (BlockPos trapPos : trap) {
			Vec3d vec3d = PositionMath.closestVec3d(enemy, trapPos);

			if (!PositionMath.isOutOfRange(enemy.getEyePos(), vec3d, config.getMiningRange().get())) {
				trapInRange.add(trapPos);
			}
		}
		return trapInRange;
	}

	private boolean isEnemy(PlayerEntity player, BlockPos pos) {
		if (player == mc.player) {
			return false;
		}
		return TargetUtils.isTargetEntity(
				PositionMath.closestVec3d(player, pos),
				player,
				config.getMiningRange().get(),
				false,
				null
		) && checkEnemyInHole(player);
	}

	private boolean checkEnemyInHole(PlayerEntity player) {
		if (!config.getTargetInHole().get()) {
			return true;
		}
		return TrapUtils.isTrapped(player, TrapType.SURROUND_WITHOUT_FLOOR);
	}

	private boolean isNormalPending(BlockPos pos, PendingBlock pendingBlock) {
		if (mc.world == null) {
			return false;
		}
		Entity enemy = mc.world.getEntityById(pendingBlock.getEnemy().getId());

		if (pendingBlock.isProtected(pos)) {
			return false;
		}
		if (isTrapMoved(pos, pendingBlock)) {
			return false;
		}
		if (!BlockUtils.isBlastResist(pos) || !BlockUtils.isBreakable(pos)) {
			return false;
		}
		if (enemy == null || enemy.isRemoved()) {
			return false;
		}
		return !PositionMath.isOutOfRange(
				enemy.getEyePos(),
				PositionMath.closestVec3d(enemy, pos),
				config.getMiningRange().get()
		);
	}

	private boolean isTrapMoved(BlockPos pos, PendingBlock pendingBlock) {
		return switch (pendingBlock.getProtect()) {
			case SURROUND -> !function.getSurround().contains(pos);
			case SELF_TRAP -> !function.getSelfTrap().contains(pos);
			case DOUBLE_SELF_TRAP -> !function.getSelfTrap().contains(pos.down());
		};
	}

	public enum Protect {
		SURROUND,
		SELF_TRAP,
		DOUBLE_SELF_TRAP;

		public boolean isCev() {
			return this == SELF_TRAP || this == DOUBLE_SELF_TRAP;
		}
	}
}
