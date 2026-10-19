package com.perfect.client.functional.function.combat.anchorAura.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.damage.DamageCalculator;
import com.perfect.client.functional.function.combat.anchorAura.AnchorAura;
import com.perfect.client.functional.function.combat.anchorAura.AnchorAuraConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class PositionHelper extends FunctionHelper<AnchorAura, AnchorAuraConfig> {
    public PositionHelper(AnchorAura anchorAura, AnchorAuraConfig config) {
        super(anchorAura, config);
    }

    public AnchorAura.PlaceInfo getPosition() {
        if (mc.player == null) return null;

        int range = config.getInteractRange().get().intValue() + 1;

        List<BlockPos> cube = PositionMath.getCube(BlockPos.ofFloored(mc.player.getEyePos()), range, range, pos -> {
            BlockState state = getState(pos);
            if (!state.isReplaceable() && !state.isOf(Blocks.RESPAWN_ANCHOR) && !config.getBreakTrap().get()) return false;
            if (state.getHardness(mc.world, pos) == -1) return false;
            return !outOfRange(pos);
        });

        if (function.attackInfo != null && cube.contains(function.attackInfo.getPos())) {
            cube.remove(function.attackInfo.getPos());
            cube.addFirst(function.attackInfo.getPos());
        }

        AnchorAura.PlaceInfo bestInfo = null;
        float bestDamage = -1;

        boolean wasInTrap = false;
        float bestBreakingSpeed = -1f;

        for (BlockPos pos : cube) {
            if (hasEntity(pos)) continue;

            BlockState state = getState(pos);

            boolean placing = state.isReplaceable();
            BlockHitResult blockHitResult = placing ? HitResultUtils.getPlaceResult(pos) : HitResultUtils.getInteractResult(pos);
            if (blockHitResult == null) continue;

            Vec3d damageVec3d = pos.toCenterPos();
            float _bestPosDamage = -1;
            AnchorAura.PlaceInfo _bestPosInfo = null;

            boolean _wasPosInTrap = false;

            float _posBreakingSpeed = state.isReplaceable() || state.isOf(Blocks.RESPAWN_ANCHOR) ? 2f : Math.clamp(
		            packetMine.getBlockStrength(pos, state), 0f, 1f);

            for (LivingEntity target : function.targets) {
                AnchorAura.PlaceInfo targetInfo = new AnchorAura.PlaceInfo(pos);
                targetInfo.setTarget(target);
                targetInfo.setState(state);

                boolean inTrap = inTrap(target, pos);
                if (_wasPosInTrap && !inTrap) continue;

                double health = target.getHealth() + target.getAbsorptionAmount();
                double minDamage = health >= config.getMinDamage().get() ? config.getMinDamage().get() : health;

                float preDamage = DamageCalculator.getExplosionDamage(target, target.getBoundingBox(), damageVec3d, config.getIgnoreTerrain().get(), false, false, 6, pos);
                if (preDamage < minDamage || ((!inTrap || _wasPosInTrap) && (preDamage <= _bestPosDamage || preDamage <= bestDamage)))
                    continue;

                float damage = DamageCalculator.getExplosionDamage(target, target.getBoundingBox(), damageVec3d, config.getIgnoreTerrain().get(), false, true, 6, pos);
                if (damage < minDamage) continue;

                if (damage > _bestPosDamage) {
                    _bestPosDamage = damage;
                    _bestPosInfo = targetInfo;
                }
                if (inTrap) _wasPosInTrap = true;
            }

            if (!isSafePlace(pos)) continue;

            if (wasInTrap && !_wasPosInTrap) continue;

            if (_wasPosInTrap) {
                if (!wasInTrap || _posBreakingSpeed > bestBreakingSpeed) {
                    bestDamage = _bestPosDamage;
                    bestInfo = _bestPosInfo;
                    wasInTrap = true;
                    bestBreakingSpeed = _posBreakingSpeed;

                    bestInfo.setHitResult(blockHitResult);
                    bestInfo.setPlacing(placing);
                } else if (_posBreakingSpeed == bestBreakingSpeed && _bestPosDamage > bestDamage) {
                    bestInfo = _bestPosInfo;
                    bestDamage = _bestPosDamage;
                    bestInfo.setHitResult(blockHitResult);
                    bestInfo.setPlacing(placing);
                }
            } else if (_bestPosDamage > bestDamage) {
                bestInfo = _bestPosInfo;
                bestDamage = _bestPosDamage;

                bestInfo.setHitResult(blockHitResult);
                bestInfo.setPlacing(placing);
            }
        }

        if (bestInfo != null) bestInfo.setInTrap(wasInTrap);
        return bestInfo;
    }

    private BlockState getState(BlockPos pos) {
        if (function.states.containsKey(pos)) return function.states.get(pos).state();
        return BlockUtils.getState(pos);
    }

    public boolean outOfRange(BlockPos pos) {
        if (BlockUtils.isOutOfBorder(pos)) {
            return true;
        }
        return PositionMath.isOutOfRange(pos, config.getInteractRange().get());
    }

    public boolean hasEntity(BlockPos pos) {
        return BlockUtils.hasEntity(new Box(pos), entity -> entity != null && entity.intersectionChecked);
    }

    private boolean inTrap(LivingEntity target, BlockPos pos) {
        List<BlockPos> trap = TrapUtils.getTrap(target, TrapType.HEAD);
        trap.addAll(TrapUtils.getTrap(target, TrapType.SURROUND));
        return trap.contains(pos);
    }

    public boolean isSafePlace(BlockPos pos) {
        if (mc.player == null) return false;

        float preDamage = DamageCalculator.getExplosionDamage(mc.player, mc.player.getBoundingBox(), pos.toCenterPos(), false, false, false, 6, pos);
        if (isNormalSelfDamage(preDamage)) return true;

        float selfDamage = DamageCalculator.getExplosionDamage(mc.player, mc.player.getBoundingBox(), pos.toCenterPos(), false, false, true, 6, pos);
        return isNormalSelfDamage(selfDamage);
    }

    public boolean isNormalSelfDamage(float damage) {
        if (mc.player == null) return false;
        return damage <= config.getMaxSelfDamage().get() && (!config.getAntiSelfPop().get() || !(mc.player.getHealth() + mc.player.getAbsorptionAmount() <= damage));
    }
}
