package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.ItemUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.State;
import com.perfect.client.functional.function.combat.autoCrystal.data.TargetInfo;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

import static com.perfect.client.core.utils.game.world.BlockUtils.*;
import static com.perfect.client.manager.system.FunctionManager.suicide;


public class TargetHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	public TargetHelper(AutoCrystal autoCrystal, AutoCrystalConfig config) {
		super(autoCrystal, config);
	}

	
	public boolean isSafeDamage(float targetDamage, float selfDamage) {
		if (suicide.isEnabled()) {
			return true;
		}

		if (mc.player == null) {
			return false;
		}
		if (selfDamage > config.getMaxSelfDamage().get()) {
			return false;
		}
		if (targetDamage != -1f && isBadRatio(targetDamage, selfDamage)) {
			return false;
		}
		return !config.getAntiSelfPop().get() || !isPopDamage(mc.player, selfDamage);
	}

	
	private boolean isPopDamage(LivingEntity entity, float damage) {
		return EntityUtils.getTotalHealth(entity) <= damage + config.getMinHealth().getFloat();
	}

	
	public boolean isBadRatio(float targetDamage, float selfDamage) {
		return targetDamage < selfDamage * config.getMinDamageRatio().get();
	}

	
	public boolean isWithoutDamage(State state, BlockPos pos, boolean placing) {
		for (LivingEntity entity : state.getTargets()) {
			if (!isWithoutDamage(entity, pos, placing)) {
				return false;
			}
		}
		return true;
	}

	
	public boolean isWithoutDamage(
			LivingEntity entity,
			EndCrystalEntity crystal,
			boolean placing
	) {
		BlockPos pos = BlockPos.ofFloored(crystal.getPos().add(0, 0.01, 0));
		return isWithoutDamage(entity, pos, placing);
	}

	
	public boolean isWithoutDamage(LivingEntity entity, BlockPos pos, boolean placing) {
		if (!placing) {
			if (!getState(pos).isFullCube(mc.world, pos) || isReplaceable(pos)) {
				return false;
			}
			if (config.getIgnoreTerrain().get() && !isBlastResist(pos)) {
				return false;
			}
		}

		Box box = getEntityBox(entity);
		return box.maxY < pos.getY() + 1;
	}

	
	public boolean shouldFacePlace(LivingEntity entity) {
		if (!config.getFacePlace().get()) {
			return false;
		}
		if (shouldArmorFacePlace(entity)) {
			return true;
		}
		return entity.getHealth() + entity.getAbsorptionAmount() <= config.getFacePlaceHp().get();
	}

	
	public boolean shouldArmorFacePlace(LivingEntity entity) {
		for (ItemStack stack : EntityUtils.getArmor(entity)) {
			if (isBrokenStack(stack)) {
				return true;
			}
		}
		return false;
	}

	
	private boolean isBrokenStack(ItemStack stack) {
		if (stack == null || stack.isEmpty()) {
			return false;
		}
		return InvUtils.getDurabilityPercentage(stack) <= config.getFacePlaceArmor().get();
	}

	
	public Box getEntityBox(LivingEntity entity) {
		if (entity.equals(mc.player)) {
			return mc.player.getBoundingBox();
		}
		return Managers.PREDICT.extrapolate(entity, config.getExtrapolation().get()).getBox();
	}

	public void fillTargetsInfo(List<LivingEntity> targets) {
		for (LivingEntity entity : targets) {
			TargetInfo targetInfo = new TargetInfo();

			targetInfo.setRegularTarget(isRegularTargetIternal(entity));
			targetInfo.setOnlyBreakCalculate(!shouldCalculatePlace(targetInfo, entity));
			function.getTargetsInfo().put(entity, targetInfo);
		}
	}

	
	private boolean shouldCalculatePlace(TargetInfo targetInfo, LivingEntity entity) {
		if (!TrapUtils.isTrapped(entity, TrapType.SURROUND, false)) {
			return true;
		}

		if (targetInfo.isRegularTarget() && !shouldFacePlace(entity)) {
			return false;
		}

		return !TrapUtils.isTrapped(entity, TrapType.FACE, false);
	}

	
	public boolean isRegularTarget(State state, LivingEntity entity) {
		return state.getTargetsInfo().get(entity).isRegularTarget();
	}

	
	public boolean isOnlyBreakCalculate(State state, LivingEntity entity) {
		return state.getTargetsInfo().get(entity).isOnlyBreakCalculate();
	}

	
	private boolean isRegularTargetIternal(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity player)) {
			return false;
		}
		for (ItemStack stack : EntityUtils.getArmor(player)) {
			if (!isRegularArmor(stack)) {
				return false;
			}
		}
		return true;
	}

	
	private boolean isRegularArmor(ItemStack stack) {
		if (!ItemUtils.isArmor(stack)) {
			return false;
		}
		if (EnchantmentUtils.getLevel(stack, Enchantments.PROTECTION) >= 4) {
			return true;
		}
		return EnchantmentUtils.getLevel(stack, Enchantments.BLAST_PROTECTION) >= 4;
	}
}