package com.perfect.client.functional.function.movement.noSlow.modes;

import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.functional.function.movement.noSlow.NoSlow;
import com.perfect.client.functional.function.movement.noSlow.NoSlowConfig;
import com.perfect.client.functional.function.movement.noSlow.utils.NoSlowMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;


public class GrimMode extends NoSlowMode {

	public GrimMode(
			NoSlow function,
			NoSlowConfig config
	) {
		super(function, config);
	}

	
	@Override
	public void onPreSendMovement() {
		if (mc.player == null) {
			return;
		}

		if (!config.getItemUse().get() || !mc.player.isUsingItem()) {
			return;
		}

		if (mc.player.getActiveHand() == Hand.OFF_HAND && checkStack(mc.player.getMainHandStack())) {
			InvUtils.useItem(Hand.MAIN_HAND, mc.player.getYaw(), mc.player.getPitch());
		}
		else if (checkStack(mc.player.getOffHandStack())) {
			InvUtils.useItem(Hand.OFF_HAND, mc.player.getYaw(), mc.player.getPitch());
		}
	}

	private boolean checkStack(ItemStack stack) {
		return !stack.getComponents().contains(DataComponentTypes.FOOD)
				&& stack.getItem() != Items.BOW
				&& stack.getItem() != Items.CROSSBOW
				&& stack.getItem() != Items.SHIELD;
	}
}
