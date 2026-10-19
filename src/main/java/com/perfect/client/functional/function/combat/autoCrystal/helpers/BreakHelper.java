package com.perfect.client.functional.function.combat.autoCrystal.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.collection.UpdatingCollection;
import com.perfect.client.core.utils.functions.CrystalUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.BreakInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.State;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class BreakHelper extends FunctionHelper<AutoCrystal, AutoCrystalConfig> {

	public BreakHelper(AutoCrystal autoCrystal, AutoCrystalConfig config) {
		super(autoCrystal, config);
	}

	
	public void attackCrystal(BreakInfo info) {
		if (info == null
				|| mc.getNetworkHandler() == null
				|| mc.player == null
				|| mc.world == null
				|| !info.getCrystal().isAlive()
		) {
			return;
		}

		if (info.isFacePlace()
				&& config.getFacePlaceSlowness().get() == AutoCrystal.Slowness.BREAK
				&& !function.getTimerHelper().isFacePlaceTimerPassed()) {
			return;
		}

		sendPacket(PlayerInteractEntityC2SPacket.attack(info.getCrystal(), mc.player.isSneaking()));
		sendPacket(new HandSwingC2SPacket(
				mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND
		));

		Managers.CRYSTAL.onAttackCrystal(info.getCrystal());

		updateExistedCrystals(info.getCrystal());
		function.getAttackPackets().put(info.getCrystal().getId(), function.getTime());
	}

	
	private void updateExistedCrystals(EndCrystalEntity entity) {
		if (!config.getInhibit().get()) {
			return;
		}

		UpdatingCollection<EndCrystalEntity> breakLimitedCrystals = function.getBreakLimitedCrystals();
		ConcurrentHashMap<EndCrystalEntity, Pair<Integer, Long>> clickedCrystals = function.getClickedCrystals();

		if (!clickedCrystals.containsKey(entity)) {
			clickedCrystals.put(entity, new Pair<>(1, function.getTime()));
		}
		else {
			Pair<Integer, Long> clicked = clickedCrystals.get(entity);
			if (clicked == null) {
				return;
			}
			int newValue = clicked.getLeft() + 1;

			if (newValue >= config.getBreakAttempts().get()) {
				breakLimitedCrystals.put(entity);
				clickedCrystals.remove(entity);
			}
			else {
				clickedCrystals.replace(entity, new Pair<>(newValue, function.getTime()));
			}
		}
	}

	public synchronized BreakInfo getBreak(State state) {
		return getBreak(state, null);
	}

	public synchronized BreakInfo getBreak(State state, EndCrystalEntity packetCrystal) {
		if (mc.world == null || mc.player == null) {
			return null;
		}

		BreakInfo bestCrystal = null;
		float bestDamage = -1;

		List<Entity> entities = CrystalUtils.getCrystalsInRange(config.getBreakRange().get());

		if (packetCrystal != null) {
			entities.remove(packetCrystal);
			entities.add(packetCrystal);
		}

		for (Entity e : entities) {
			if (!(e instanceof EndCrystalEntity crystal)) {
				continue;
			}
			if (isInvalidBreak(state, crystal)) {
				continue;
			}
			if (isObstructiveForPlace(crystal)) {
				BreakInfo breakInfo = new BreakInfo();
				breakInfo.setCrystal(crystal);
				breakInfo.setFacePlace(false);

				return breakInfo;
			}

			float bestCrystalDamage = -1;
			BreakInfo bestCrystalBreak = null;

			for (LivingEntity entity : state.getTargets()) {
				Box targetBox = function.getTargetHelper().getEntityBox(entity);

				BreakInfo targetBreakInfo = new BreakInfo();
				targetBreakInfo.setCrystal(crystal);

				if (function.getTargetHelper().isWithoutDamage(entity, crystal, false)) {
					continue;
				}

				float minDamage = function.getDamageHelper().getMinDamageForTarget(entity);
				float preDamage = getDamage(entity, targetBox, crystal.getPos(), false);

				if (preDamage < minDamage || preDamage <= bestCrystalDamage || preDamage <= bestDamage) {
					continue;
				}

				float damage = getDamage(entity, targetBox, crystal.getPos(), true);

				if (damage < minDamage) {
					if (function.getTargetHelper().shouldFacePlace(entity)) {
						targetBreakInfo.setFacePlace(true);
						minDamage = 1.5f;
					}
					else {
						continue;
					}
				}

				if (damage < minDamage) {
					continue;
				}

				if (damage > bestCrystalDamage) {
					bestCrystalDamage = damage;
					bestCrystalBreak = targetBreakInfo;
				}
			}
			if (bestCrystalDamage > bestDamage && isSafeBreak(bestCrystalDamage, crystal)) {
				bestCrystal = bestCrystalBreak;
				bestDamage = bestCrystalDamage;
			}
		}

		return bestCrystal;
	}

	
	private float getDamage(LivingEntity entity, Box entityBox, Vec3d crystalPos, boolean exposure) {
		return function.getDamageHelper().getDamage(entity, entityBox, crystalPos, false, exposure);
	}

	
	public boolean cantBreak(State state, EndCrystalEntity entity) {
		if (!config.getInhibit().get()) {
			return false;
		}
		if (!state.getExistedCrystals().containsKey(entity)) {
			return false;
		}
		if (state.getCompletedCrystals().containsKey(entity)) {
			return true;
		}
		if (state.getExistedCrystals().get(entity).getLeft() >= config.getBreakAttempts().get()) {
			return true;
		}
		return state.getWorkTimeMs() - state.getExistedCrystals().get(entity).getRight() < config.getRetryDelay().get();
	}

	
	public boolean isInvalidBreak(State state, EndCrystalEntity crystal) {
		assert mc.player != null;

		if (crystal.isRemoved() || cantBreak(state, crystal)) {
			return true;
		}

		if (config.getWallsBreakRange().isEnabled() && !mc.player.canSee(crystal)) {
			return PositionMath.isOutOfRange(crystal.getPos(), config.getWallsBreakRange().get());
		}

		return PositionMath.isOutOfRange(crystal.getPos(), config.getBreakRange().get());
	}

	
	private boolean isSafeBreak(float targetDamage, EndCrystalEntity crystal) {
		if (mc.player == null) {
			return false;
		}

		if (function.getTargetHelper().isWithoutDamage(mc.player, crystal, false)) {
			return true;
		}

		float preDamage = function.getDamageHelper().getSelfDamage(crystal.getPos(), false, false);

		if (function.getTargetHelper().isSafeDamage(targetDamage, preDamage)) {
			return true;
		}

		float selfDamage = function.getDamageHelper().getSelfDamage(crystal.getPos(), false, true);
		return function.getTargetHelper().isSafeDamage(targetDamage, selfDamage);
	}

	private boolean isObstructiveForPlace(EndCrystalEntity crystal) {
		if (function.getPlaceInfo() == null) {
			return false;
		}
		return crystal.equals(function.getPlaceInfo().getObstructive());
	}
}