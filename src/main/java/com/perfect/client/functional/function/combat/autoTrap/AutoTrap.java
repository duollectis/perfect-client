package com.perfect.client.functional.function.combat.autoTrap;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunction;
import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.utils.functions.PlaceUtils;
import com.perfect.client.core.utils.functions.target.TargetPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.SlimeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;


public class AutoTrap extends PlaceFunction {

	private final AutoTrapConfig config = new AutoTrapConfig(this);

	public AutoTrap() {
		super("auto-trap", "Auto Trap", Category.COMBAT, true, false);
	}

	@Override
	protected PlaceFunctionConfig<AutoTrap> getConfig() {
		return config;
	}



	@Override
	public String getStatus() {
		// [Artik]

		LivingEntity target = getTarget();

		if (target == null || mc.world == null || mc.player == null) {
			return "None";
		}

		String targetName = target.getType().getName().getString();
		if (target instanceof PlayerEntity player) {
			targetName = player.getGameProfile().getName();
		}

		return targetName;
	}

	@Override
	protected List<BlockPos> getTrap() {
		LivingEntity target = getTarget();

		if (target == null) {
			return null;
		}

		List<BlockPos>
				trap =
				TrapUtils.getTrap(
						target,
						getTrapType(),
						AbstractTorchBlock.class,
						SlimeBlock.class,
						HoneyBlock.class
				);

		if (config.getAntiStep().get()) {
			addAntiStep(trap, target);
		}

		return trap;
	}

	private TrapType getTrapType() {
		if (config.getAntiStep().get()) {
			return TrapType.TRAP_WITH_ANTI_STEP;
		}
		else {
			return TrapType.TRAP;
		}
	}

	private void addAntiStep(List<BlockPos> trap, LivingEntity target) {
		List<BlockPos> antiStepBlocks = new ArrayList<>();

		int headY = MathHelper.floor(target.getEyePos().getY());

		for (BlockPos pos : trap) {
			if (pos.getY() != headY) {
				continue;
			}
			BlockPos antiStepPos = pos.up();
			antiStepBlocks.add(antiStepPos);
		}

		trap.addAll(antiStepBlocks);
	}

	private LivingEntity getTarget() {
		return TargetUtils.getTarget(
				config.getTargetRange().get(),
				TargetPriority.FIELD_OF_VIEW,
				false,
				this::isGoodTarget
		);
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return false;
		}
		return !TrapUtils.isTrapped(entity, config.getMode().get().getType(), false);
	}

	@Override
	protected EndCrystalEntity getCrystal(List<BlockPos> positions) {
		for (BlockPos pos : positions) {
			if (!BlockUtils.isReplaceable(pos)) {
				continue;
			}

			EndCrystalEntity crystal = PlaceUtils.getCrystalByBlock(pos);

			if (crystal != null) {
				return crystal;
			}
		}

		return null;
	}

	@Override
	protected PlaceInfo getInstantProtectInfo(EndCrystalEntity crystal) {
		return null;
	}
}
