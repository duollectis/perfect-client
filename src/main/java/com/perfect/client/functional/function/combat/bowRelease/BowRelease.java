package com.perfect.client.functional.function.combat.bowRelease;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Items;

public class BowRelease extends Function {

	private final BowReleaseConfig config = new BowReleaseConfig(this);

	private boolean isAutoPressing = false;

	public BowRelease() {
		super("bow-release", "Bow Release", Category.COMBAT, "Bow Spam");
	}

	@Override
	public void onEnable() {
		resetState();
	}

	@Override
	public void onDisable() {
		resetState();
	}

	@Override
	public void onPostTick() {
		if (shouldSkip()) {
			if (isAutoPressing) {
				resetState();
			}

			return;
		}

		if (isHoldingBow()) {
			processBowSpam();
		} else if (isAutoPressing) {
			resetState();
		}
	}

	private void processBowSpam() {
		assert mc.player != null;
		assert mc.interactionManager != null;

		int useTime = mc.player.getItemUseTime();
		int threshold = config.getCharge().get();

		if (useTime >= threshold) {
			mc.interactionManager.stopUsingItem(mc.player);
			isAutoPressing = false;
		} else {
			updateMouseState(true);
		}
	}

	private boolean shouldSkip() {
		if (mc.player == null || mc.interactionManager == null) {
			return true;
		}

		if (!InvUtils.find(stack -> stack.getItem() instanceof ArrowItem).found()) {
			return true;
		}

		return config.getOnlyRightClick().get() && !mc.options.useKey.isPressed();
	}

	private boolean isHoldingBow() {
		assert mc.player != null;

		return mc.player.getMainHandStack().getItem() == Items.BOW;
	}

	private void updateMouseState(boolean pressed) {
		mc.options.useKey.setPressed(pressed);
		isAutoPressing = pressed;
	}

	private void resetState() {
		updateMouseState(false);
	}
}
