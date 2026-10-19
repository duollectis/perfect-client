package com.perfect.client.functional.function.player.autoExp;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.delay.TickDelayUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Getter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.ArrayList;
import java.util.List;


public class AutoExp extends Function {

	private static final int AVERAGE_EXPERIENCE = 12;

	private final AutoExpConfig config = new AutoExpConfig(this);

	@Getter
	private final TickDelayUtils delay = new TickDelayUtils(() -> config.getDelay().get());

	private final List<TimerUtils> thrown = new ArrayList<>();

	private Runnable postRotateAction;
	private boolean throwing;

	public AutoExp() {
		super("auto-exp", "Auto Exp", Category.PLAYER, "Exp Thrower", "Auto Mending", "Fast Exp");
	}

	@Override
	public void onRotate() {
		throwing = false;
		postRotateAction = null;

		if (mc.interactionManager == null || mc.player == null) {
			return;
		}

		updateTimers();
		FindItemResult exp = InvUtils.findInHotbar(Items.EXPERIENCE_BOTTLE);

		if (!exp.found()) {
			return;
		}
		if (!shouldExp()) {
			if (config.getDisableAfterMending().get()) {
				disable();
			}

			return;
		}

		throwing = true;

		postRotateAction = () -> {
			if (exp.getHand() != null) {
				throwExp();
			}
			else {
				InvUtils.setSlot(exp.slot(), true);
				throwExp();
				InvUtils.swapBack();
			}
		};

		if (config.getRotate().get()) {
			Managers.ROTATION.setRotatePitch(config.getPitch().getFloat());
		}
	}

	@Override
	public void onPostSendMovement() {
		if (mc.interactionManager == null || mc.player == null) {
			return;
		}

		if (postRotateAction != null && delay.isPassed()) {
			postRotateAction.run();
		}

		delay.tick();
	}

	public boolean isThrowing() {
		return isEnabled() && throwing;
	}

	private boolean shouldExp() {
		if (mc.player == null) {
			return false;
		}
		if (!config.getNoUnnecessary().get()) {
			return true;
		}

		List<ItemStack> stacks = new ArrayList<>();
		addDamageable(stacks, mc.player.getMainHandStack());
		addDamageable(stacks, mc.player.getEquippedStack(EquipmentSlot.FEET));
		addDamageable(stacks, mc.player.getEquippedStack(EquipmentSlot.LEGS));
		addDamageable(stacks, mc.player.getEquippedStack(EquipmentSlot.CHEST));
		addDamageable(stacks, mc.player.getEquippedStack(EquipmentSlot.HEAD));
		addDamageable(stacks, mc.player.getOffHandStack());

		for (ItemStack stack : stacks) {
			if (isGoodStack(stack)) {
				return true;
			}
		}

		return false;
	}

	private boolean isGoodStack(ItemStack stack) {
		return stack.getMaxDamage() - stack.getDamage() + (thrown.size() + 1) * AVERAGE_EXPERIENCE
				<= stack.getMaxDamage();
	}

	private void addDamageable(List<ItemStack> list, ItemStack stack) {
		if (stack.isDamageable()) {
			list.add(stack);
		}
	}

	private void updateTimers() {
		List<TimerUtils> toRemove = new ArrayList<>();
		thrown.forEach(timer -> {
			if (timer.passedMillis(Managers.PING.getPing() + 300)) {
				toRemove.add(timer);
			}
		});
		toRemove.forEach(thrown::remove);
	}

	private void throwExp() {
		if (mc.interactionManager == null || mc.player == null) {
			return;
		}

		float pitch = config.getRotate().get() ? config.getPitch().get().floatValue() : mc.player.getPitch();

		for (int i = 0; i < config.getPerTick().get(); i++) {
			if (!shouldExp()) {
				break;
			}

			InvUtils.useItem(Hand.MAIN_HAND, mc.player.getYaw(), pitch);

			thrown.add(new TimerUtils());
		}
	}
}
