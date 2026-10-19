package com.perfect.client.functional.function.misc.noInteract;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class NoInteract extends Function {

	private final NoInteractConfig config = new NoInteractConfig(this);

	public NoInteract() {
		super("no-interact", "No Interact", Category.MISC);
	}

	public boolean shouldCancelInteract() {
		if (mc.player == null || !isEnabled()) {
			return false;
		}

		if (config.getMainHand().get() && checkItem(mc.player.getMainHandStack())) {
			return true;
		}

		return config.getOffHand().get() && checkItem(mc.player.getOffHandStack());
	}

	private boolean checkItem(ItemStack stack) {
		Item item = stack.getItem();

		if (config.getEat().get() && item.getComponents().contains(DataComponentTypes.FOOD)) {
			return true;
		}

		return config.getXp().get() && item == Items.EXPERIENCE_BOTTLE;
	}
}
