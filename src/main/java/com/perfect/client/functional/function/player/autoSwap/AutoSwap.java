package com.perfect.client.functional.function.player.autoSwap;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.math.SecureRandomUtils;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

import java.util.function.Predicate;

public class AutoSwap extends Function {

	private final AutoSwapConfig config = new AutoSwapConfig(this);

	private SwapItem pressed;
	private int swapBackSlot;
	private int swapTicks;
	private int swapBackTicks;
	private boolean swapped;

	private boolean stopMovement;

	public AutoSwap() {
		super("auto-swap", "Auto Swap", Category.PLAYER);
	}

	public void setItem(SwapItem item) {
		if (pressed == null)
			pressed = item;
	}

	@Override
	public void onEnable() {
		swapBackSlot = -1;
		pressed = null;
		startMovement();
	}

	@Override
	public void onHandleInput() {
		if (mc.player == null ||
				mc.world == null ||
				mc.interactionManager == null ||
				(!config.getInGui().get() && mc.currentScreen != null)
		) {
			reset();
			return;
		}

		if (swapBackSlot != -1 && swapTicks > config.getTicksPre().get()) {
			if (swapBackTicks < config.getTicksPost().get()) {
				swapBackTicks++;
				return;
			}

			if (swapBackSlot > 8) {
				InvUtils.clickSlot(
						SlotUtils.getSlotID(swapBackSlot),
						mc.player.getInventory().getSelectedSlot(),
						SlotActionType.SWAP
				);
			}
			else {
				InvUtils.setSlot(swapBackSlot, false);
			}

			startMovement();
			swapBackSlot = -1;
			reset();
			return;
		}

		if (pressed == null) {
			reset();
			return;
		}

		FindItemResult result = InvUtils.find(stack -> pressed.isGoodStack(stack));

		if (!result.found() || (!config.getFromInventory().get() && result.slot() > 8)) {
			reset();
			return;
		}

		if (config.getBypassSprint().get() && Managers.NETWORK.isSprinting()) {
			Managers.PLAYER.setSprintDelay(SecureRandomUtils.random(2, 3));
			return;
		}

		boolean swap = result.slot() != mc.player.getInventory().getSelectedSlot();
		Hand hand = result.slot() == SlotUtils.OFFHAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		int prevSlot = mc.player.getInventory().getSelectedSlot();

		if (swapped && config.getBypassTime().get()) {
			swapTicks++;

			if (swapTicks <= config.getTicksPre().get()) {
				return;
			}
		}

		if (!swapped && swap) {
			if (result.slot() <= 8) {
				InvUtils.setSlot(result.slot(), false);
			}
			else {
				InvUtils.clickSlot(
						SlotUtils.getSlotID(result.slot()),
						mc.player.getInventory().getSelectedSlot(),
						SlotActionType.SWAP
				);
			}

			if (config.getBypassMovement().get()) {
				stopMovement();
			}
		}

		if (config.getBypassTime().get() && !swapped) {
			swapped = true;
			swapTicks = 0;
			swapBackTicks = 0;

			if (result.slot() > 8) {
				swapBackSlot = result.slot();
			}
			else {
				swapBackSlot = prevSlot;
			}

			return;
		}

		InvUtils.useItem(hand, mc.player.getYaw(), mc.player.getPitch());

		if (swap && !config.getBypassTime().get()) {
			if (result.slot() > 8) {
				InvUtils.clickSlot(
						SlotUtils.getSlotID(result.slot()),
						mc.player.getInventory().getSelectedSlot(),
						SlotActionType.SWAP
				);
			}
			else {
				InvUtils.setSlot(prevSlot, false);
			}
		}

		reset();
		swapped = false;

		if (!config.getBypassTime().get()) {
			startMovement();
		}
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.world == null) {
			return;
		}

		if (Managers.KEY_STATE.isKeyboardPressed(GLFW.GLFW_KEY_F3)) {
			return;
		}

		if (!config.getBypassMovement().get() || !stopMovement) {
			return;
		}

		event.setAllInput(false);
	}

	private void startMovement() {
		stopMovement = false;
	}

	private void stopMovement() {
		stopMovement = true;
	}

	private void reset() {
		pressed = null;
	}

	@Getter
	public enum SwapItem {
		PEARL(Items.ENDER_PEARL, null),
		TRAP(Items.NETHERITE_SCRAP, stack -> stack.getName().getString().contains("Трапка")),
		LAYER(Items.DRIED_KELP, stack -> stack.getName().getString().contains("Пласт")),
		DISORIENT(Items.ENDER_EYE, stack -> stack.getName().getString().contains("Дезориентация")),
		FREEZING_BALL(Items.SNOWBALL, stack -> stack.getName().getString().contains("Снежок заморозка")),
		GODS_AURA(Items.PHANTOM_MEMBRANE, stack -> stack.getName().getString().contains("Божья аура")),
		VISIBLE_DUST(Items.SUGAR, stack -> stack.getName().getString().contains("Явная пыль"));

		private final Item item;
		private final Predicate<ItemStack> predicate;

		SwapItem(Item item, Predicate<ItemStack> predicate) {
			this.item = item;
			this.predicate = predicate;
		}

		public boolean isGoodStack(ItemStack stack) {
			if (stack == null) {
				return false;
			}

			if (predicate == null) {
				return stack.isOf(item);
			}
			else {
				return stack.isOf(item) && predicate.test(stack);
			}
		}
	}
}
