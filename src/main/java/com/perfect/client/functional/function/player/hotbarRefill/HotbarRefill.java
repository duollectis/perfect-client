package com.perfect.client.functional.function.player.hotbarRefill;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.gui.widgets.ClickGuiScreen;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;

import java.util.function.Predicate;

public class HotbarRefill extends Function {

	private final HotbarRefillConfig config = new HotbarRefillConfig(this);

	private final ItemStack[] items;

	public HotbarRefill() {
		super("hotbar-refill", "Hotbar Refill", Category.PLAYER, "Hotbar Replenish");

		items = new ItemStack[10];
		updateItems(true);
	}

	@Override
	public void onEnable() {
		if (mc.player == null) {
			return;
		}

		updateItems(false);
	}

	@Override
	public void onPostTick() {
		if (mc.player == null) {
			return;
		}

		if (mc.currentScreen == null
				|| mc.currentScreen instanceof ClickGuiScreen
				|| mc.currentScreen instanceof ChatScreen) {
			for (int i = 0; i < 9; i++) {
				ItemStack stack = InvUtils.getStack(i);
				checkSlot(i, stack);
			}

			if (config.getOffhand().get()) {
				ItemStack stack = mc.player.getOffHandStack();
				checkSlot(9, stack);
			}
		}

		updateItems(false);
	}

	private void checkSlot(int slot, ItemStack stack) {
		ItemStack prevStack = items[slot];

		if (stack.isStackable() && !stack.isEmpty() && stack.getCount() <= config.getCount().get()) {
			refill(slot);
		}

		//if (result.isEmpty() && !prevStack.isEmpty()) {
		//    if (prevStack.isStackable()) refill(slot);
		//    else if (singleItems.get()) refill(slot);
		//}
	}

	private void refill(int slot) {
		if (mc.interactionManager == null || mc.player == null)
			return;

		ItemStack prevStack = items[slot];
		Item item = prevStack.getItem();

		boolean offhand = slot == 9;

		if (offhand) {
			slot = SlotUtils.OFFHAND;
		}

		FindItemResult result;

		if (isShulkerItem(item)) {
			result = findItem(stack -> isShulkerItem(stack.getItem()), slot);
		}
		else if (isBedItem(item)) {
			result = findItem(stack -> isBedItem(stack.getItem()), slot);
		}
		else {
			result = findItem(stack -> stack.getItem() == item, slot);
		}

		if (!result.found()) {
			return;
		}

		int slotID = SlotUtils.getSlotID(result.slot());
		int button = SlotUtils.getSlotID(slot);

		ItemStack currentStack = InvUtils.getStack(slot);
		boolean needBack = result.stack().getCount() + currentStack.getCount() > currentStack.getMaxCount();

		if (mc.player.currentScreenHandler.getCursorStack().getItem() != result.stack().getItem()) {
			InvUtils.clickSlot(slotID, 0, SlotActionType.PICKUP);
		}
		InvUtils.clickSlot(button, 0, SlotActionType.PICKUP);

		if (needBack)
			InvUtils.clickSlot(slotID, 0, SlotActionType.PICKUP);
	}

	public FindItemResult findItem(Predicate<ItemStack> isGood, int excludedSlot) {
		if (mc.player == null)
			return new FindItemResult(null, -1);
		int slot = -1;
		ItemStack foundStack = null;
		for (int i = mc.player.getInventory().size(); i >= 9; i--) {
			if (i == excludedSlot || !SlotUtils.isMain(i))
				continue;

			ItemStack stack = InvUtils.getStack(i);

			if (isGood.test(stack)) {
				if (slot == -1) {
					slot = i;
					foundStack = stack;
				}
			}
		}
		return new FindItemResult(foundStack, slot);
	}

	private void updateItems(boolean empty) {
		if (mc.player == null)
			empty = true;

		for (int i = 0; i < 9; i++) {
			items[i] = empty ? new ItemStack(Items.AIR) : InvUtils.getStack(i);
		}

		items[9] = empty ? new ItemStack(Items.AIR) : mc.player.getOffHandStack();
	}

	private boolean isShulkerItem(Item item) {
		return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
	}

	private boolean isBedItem(Item item) {
		return item instanceof BedItem;
	}
}
