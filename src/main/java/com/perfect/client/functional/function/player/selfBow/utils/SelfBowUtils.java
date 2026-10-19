package com.perfect.client.functional.function.player.selfBow.utils;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.functional.function.player.selfBow.SelfBow;
import com.perfect.client.functional.function.player.selfBow.SelfBowConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class SelfBowUtils extends FunctionHelper<SelfBow, SelfBowConfig> {

	private static final float PITCH_FACTOR = 12.0F;
	private static final double JUMP_FACTOR = 0.65;

	public SelfBowUtils(SelfBow function, SelfBowConfig config) {
		super(function, config);
	}

	public void resetPlayerAction() {
		assert mc.interactionManager != null;

		mc.options.useKey.setPressed(false);
		mc.interactionManager.stopUsingItem(mc.player);
	}

	public void scanAndFillArrows() {
		assert mc.player != null;

		function.getArrowSlots().clear();
		List<StatusEffect> usedEffects = new ArrayList<>();
		int currentSelected = mc.player.getInventory().getSelectedSlot();

		for (int i = 0; i < mc.player.getInventory().size(); i++) {
			if (i == currentSelected) {
				continue;
			}

			ItemStack item = mc.player.getInventory().getStack(i);
			if (item.getItem() != Items.TIPPED_ARROW) {
				continue;
			}

			PotionContentsComponent contents = item.get(DataComponentTypes.POTION_CONTENTS);
			if (contents == null || contents.getEffects() == null) {
				continue;
			}

			int finalI = i;
			contents.getEffects().forEach(effectInstance -> {
				StatusEffect effect = effectInstance.getEffectType().value();

				if (isEffectValid(effect, usedEffects)) {
					usedEffects.add(effect);
					function.getArrowSlots().add(finalI);
				}
			});
		}

		if (function.getArrowSlots().isEmpty()) {
			disable("Arrow not found");
			return;
		}
	}

	public boolean isEffectValid(StatusEffect effect, List<StatusEffect> usedEffects) {
		return containsEffect(effect)
				&& !usedEffects.contains(effect)
				&& (!hasEffect(effect) || !config.getCheckEffects().get());
	}

	public void prepareArrowToSlot() {
		InvUtils.clickSlot(SlotUtils.getSlotID(function.getArrowSlots().getFirst()), 0, SlotActionType.PICKUP);
		InvUtils.clickSlot(SlotUtils.getSlotID(9), 0, SlotActionType.PICKUP);

		mc.options.useKey.setPressed(true);
	}

	public void processBowShot() {
		assert mc.player != null;

		int useTicks = mc.player.getItemUseTime();
		if (useTicks < config.getCharge().get()) {
			return;
		}

		int targetSlot = function.getArrowSlots().removeFirst();

		float yaw = mc.player.getYaw();
		float pitch = -90.0F;

		float forward = mc.player.input.getMovementInput().y;
		float strafe = mc.player.input.getMovementInput().x;

		if (forward != 0.0F || strafe != 0.0F) {
			float moveYaw = yaw;

			if (forward != 0.0F) {
				if (strafe > 0.0F) {
					moveYaw += (forward > 0.0F ? -45 : 45);
				}
				else if (strafe < 0.0F) {
					moveYaw += (forward > 0.0F ? 45 : -45);
				}

				if (forward < 0.0F) {
					moveYaw += 180.0F;
				}
			} else {
				if (strafe > 0.0F) {
					moveYaw -= 90.0F;
				}
				else if (strafe < 0.0F) {
					moveYaw += 90.0F;
				}
			}
			yaw = moveYaw;

			double speed = Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);

			if (!mc.player.isOnGround()) {
				speed *= JUMP_FACTOR;
			}

			float pitchOffset = (float) (90.0 * speed) / useTicks;

			pitch = -90.0F + pitchOffset * PITCH_FACTOR;
		}

		pitch = Math.clamp(pitch, -90.0f, 90.0f);

		sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
				yaw, pitch, mc.player.isOnGround(), mc.player.horizontalCollision
		));

		resetPlayerAction();

		if (targetSlot != 9) {
			InvUtils.clickSlot(SlotUtils.getSlotID(9), 0, SlotActionType.PICKUP);
			InvUtils.clickSlot(SlotUtils.getSlotID(targetSlot), 0, SlotActionType.PICKUP);
		}

		function.getTimer().reset();
	}

	public boolean check() {
		assert mc.player != null;

		if (!function.getBow().found()) {
			disable("Bow not found");
			return true;
		}

		if (!function.getBow().isHotbar()) {
			disable("Bow is not in hotbar");
			return true;
		}

		if (EntityUtils.getTotalHealth(mc.player) < config.getMinHealth().get()) {
			disable("Min health exceeded");
			return true;
		}

		return false;
	}

	public boolean hasEffect(StatusEffect effect) {
		assert mc.player != null;

		return mc.player.getStatusEffects().stream()
		                .anyMatch(instance -> instance.getEffectType().value().equals(effect));
	}

	public boolean containsEffect(StatusEffect effect) {
		return Stream.of(
				Map.entry(SelfBow.Effects.SPEED, StatusEffects.SPEED),
				Map.entry(SelfBow.Effects.STRENGTH, StatusEffects.STRENGTH),
				Map.entry(SelfBow.Effects.JUMP_BOOST, StatusEffects.JUMP_BOOST),
				Map.entry(SelfBow.Effects.REGENERATION, StatusEffects.REGENERATION)
		).anyMatch(entry -> config.getEffects().get(entry.getKey()) && entry.getValue().value() == effect);
	}

	public void disable(String msg) {
		function.error(msg);
		function.disable();
	}
}
