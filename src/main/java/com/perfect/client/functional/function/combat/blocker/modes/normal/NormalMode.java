package com.perfect.client.functional.function.combat.blocker.modes.normal;

import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.CrystalUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.blocker.Blocker;
import com.perfect.client.functional.function.combat.blocker.BlockerConfig;
import com.perfect.client.functional.function.combat.blocker.data.PostProtectInfo;
import com.perfect.client.functional.function.combat.blocker.helpers.BlockerMode;
import com.perfect.client.manager.game.mining.PlayerBreakInfo;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.breakESP;


public class NormalMode extends BlockerMode {

	private final List<PostProtectInfo> postProtectInfos = new ArrayList<>();

	public NormalMode(Blocker function, BlockerConfig config) {
		super(function, config);
	}

	
	@Override
	public void addTrap(List<BlockPos> list) {
		assert mc.player != null;

		for (PlayerBreakInfo info : Managers.BREAK_INFO.getAll()) {
			BlockPos pos = info.getPos();

			if (info.isFriend()) {
				continue;
			}

			if (list.contains(pos)) {
				continue;
			}

			if (isOutOfRange(pos.toCenterPos())) {
				continue;
			}

			if (info.getProgress(breakESP.getSpeed()) < config.getBreakProgress().fromPercent()) {
				continue;
			}

			Blocker.ProtectMode mode = getProtectMode(pos);

			if (mode == null) {
				continue;
			}

			function.addBlockProtect(list, mode, pos);
		}

		if (config.getProtection().get().selfTrap()) {
			List<Entity> crystals = CrystalUtils.getCrystalsInRange(config.getRange().get());

			for (Entity crystal : crystals) {

				BlockPos blockPos = crystal.getBlockPos().down();
				Vec3d damageVec3d = crystal.getPos();

				if (needProtectCrystal(blockPos, damageVec3d)) {
					function.addBlockProtect(list, Blocker.ProtectMode.SELF_TRAP, blockPos);
				}
			}
		}

		for (PostProtectInfo postProtectInfo : postProtectInfos) {

			BlockPos blockPos = postProtectInfo.getBlockPos();
			Vec3d damageVec3d = postProtectInfo.getDamageVec3d();

			if (needProtectCrystal(blockPos, damageVec3d)) {
				function.addBlockProtect(list, Blocker.ProtectMode.SELF_TRAP, blockPos);
			}
		}

		postProtectInfos.clear();
	}

	@Override
	public void onEntityRemove(EntityEvent event) {
		if (!(event.getEntity() instanceof EndCrystalEntity crystal)) {
			return;
		}
		if (isOutOfRange(crystal.getPos())) {
			return;
		}

		postProtectInfos.add(new PostProtectInfo(crystal.getBlockPos().down(), crystal.getPos()));
	}

	private boolean isOutOfRange(Vec3d pos) {
		return PositionMath.isOutOfRange(pos, config.getRange().get() + 2);
	}

	
	private boolean needProtectCrystal(BlockPos blockPos, Vec3d damageVec3d) {
		if (function.getSelfTrap().contains(blockPos)) {
			return true;
		}
		return config.getExtraPositions().get() && isDamageCrystal(damageVec3d) && isUpper(blockPos);
	}

	
	private Blocker.ProtectMode getProtectMode(BlockPos pos) {
		if (mc.player == null || pos == null) {
			return null;
		}

		if (config.getProtection().get().surround() && function.getSurround().contains(pos)) {
			return Blocker.ProtectMode.SURROUND;
		}
		if (config.getProtection().get().selfTrap()) {
			BlockState state = BlockUtils.getState(pos);

			if (state.isReplaceable() && !config.getAirTrigger().get()) {
				if (!BlockUtils.isBlock(state)) {
					return null;
				}
			}

			if (function.getSelfTrap().contains(pos)) {
				return Blocker.ProtectMode.SELF_TRAP;
			}

			if (config.getExtraPositions().get() && isDamageBlock(pos)) {
				return Blocker.ProtectMode.SELF_TRAP;
			}
		}
		return null;
	}

	private boolean isUpper(BlockPos pos) {
		assert mc.player != null;
		return pos.getY() - Math.floor(mc.player.getEyePos().getY()) >= 0;
	}

	private boolean isDamageBlock(BlockPos pos) {
		return getBlockDamage(pos) > config.getMinDamage().get();
	}

	private double getBlockDamage(BlockPos pos) {
		if (mc.player == null) {
			return 0.0f;
		}

		return DamageCalculator.getExplosionDamage(
				mc.player,
				mc.player.getBoundingBox(),
				BlockUtils.getCrystalVec3d(pos),
				true,
				false,
				true,
				6,
				pos
		);
	}

	private boolean isDamageCrystal(Vec3d pos) {
		return getCrystalDamage(pos) > config.getMinDamage().get();
	}

	private double getCrystalDamage(Vec3d pos) {
		if (mc.player == null) {
			return 0.0f;
		}

		return DamageCalculator.getExplosionDamage(
				mc.player,
				mc.player.getBoundingBox(),
				pos,
				true,
				false,
				true,
				6,
				null
		);
	}
}
