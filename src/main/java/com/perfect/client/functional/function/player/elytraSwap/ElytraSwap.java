package com.perfect.client.functional.function.player.elytraSwap;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;


public class ElytraSwap extends Function {

	private static final int CHEST_SLOT = 6;

	private final ElytraSwapConfig config = new ElytraSwapConfig(this);

	public ElytraSwap() {
		super("elytra-swap", "Elytra Swap", Category.PLAYER);
	}

	@Override
	public void onEnable() {
		action();
		disable();
	}

	
	private void action() {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		ItemStack stack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
		boolean wearingChestPlate = isChestPlate(stack.getItem());

		if (!wearingChestPlate && !stack.isOf(Items.ELYTRA)) {
			return;
		}

		FindItemResult fir = wearingChestPlate ? InvUtils.find(Items.ELYTRA) : findChestPlate();
		if (!fir.found()) {
			return;
		}

		int slotID = SlotUtils.getSlotID(fir.slot());

		boolean sprinting = Managers.NETWORK.isSprinting();

		if (sprinting) {
			sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
		}

		switch (config.getSwap().get()) {
			case ALTERNATIVE -> alternativeSwap(slotID, wearingChestPlate);
			case SEMI_ALT -> semiAltSwap(slotID, wearingChestPlate);
			case PICK -> pickSwap(slotID, wearingChestPlate);
			case QUICK_MOVE -> quickMoveSwap(slotID);
		}

		if (sprinting) {
			sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
		}
	}

	
	private void alternativeSwap(int slot, boolean wearingChestPlate) {
		assert mc.player != null;

		boolean needSwap = needSwap(mc.player.getMainHandStack(), wearingChestPlate);

		int selected = mc.player.getInventory().getSelectedSlot();

		if (needSwap) {
			InvUtils.clickSlot(slot, selected, SlotActionType.SWAP);
		}

		InvUtils.clickSlot(CHEST_SLOT, selected, SlotActionType.SWAP);

		if (needSwap) {
			InvUtils.clickSlot(slot, selected, SlotActionType.SWAP);
		}
	}

	
	private void semiAltSwap(int slot, boolean wearingChestPlate) {
		assert mc.player != null;

		boolean needSwap = needSwap(mc.player.getMainHandStack(), wearingChestPlate);

		int selected = mc.player.getInventory().getSelectedSlot();

		if (needSwap) {
			InvUtils.clickSlot(slot, selected, SlotActionType.SWAP);
		}

		InvUtils.useItem(Hand.MAIN_HAND, mc.player.getYaw(), mc.player.getPitch());

		if (needSwap) {
			InvUtils.clickSlot(slot, selected, SlotActionType.SWAP);
		}
	}

	
	private void pickSwap(int slot, boolean wearingChestPlate) {
		assert mc.player != null;

		boolean needPickup = needSwap(mc.player.currentScreenHandler.getCursorStack(), wearingChestPlate);

		if (needPickup) {
			InvUtils.clickSlot(slot, 0, SlotActionType.PICKUP);
		}

		InvUtils.clickSlot(CHEST_SLOT, 0, SlotActionType.PICKUP);

		int backSlot = slot;

		if (!needPickup) {
			FindItemResult empty = InvUtils.findFromEnd(ItemStack::isEmpty);
			if (empty.found()) {
				backSlot = SlotUtils.getSlotID(empty.slot());
			}
			else {
				backSlot = InvUtils.getFirstHotbarSlotId() + 1;
			}
		}

		InvUtils.clickSlot(backSlot, 0, SlotActionType.PICKUP);
	}

	
	private void quickMoveSwap(int slot) {
		assert mc.player != null;

		InvUtils.clickSlot(CHEST_SLOT, 0, SlotActionType.PICKUP);
		InvUtils.clickSlot(slot, 0, SlotActionType.QUICK_MOVE);
		InvUtils.clickSlot(slot, 0, SlotActionType.PICKUP);
	}

	private boolean needSwap(ItemStack stack, boolean wearingChestPlate) {
		if (wearingChestPlate) {
			return !stack.isOf(Items.ELYTRA);
		}
		return !isChestPlate(stack.getItem());
	}

	private FindItemResult findChestPlate() {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		int slot = -1;
		int bestPriority = -1;
		ItemStack foundStack = null;

		for (int i = 0; i <= mc.player.getInventory().size(); i++) {
			ItemStack stack = InvUtils.getStack(i);

			int priority = getPriority(stack);

			if (priority == -1) {
				continue;
			}

			if (slot == -1 || bestPriority < priority) {
				slot = i;
				bestPriority = priority;
				foundStack = stack;
			}
		}

		return new FindItemResult(foundStack, slot);
	}

	private int getPriority(ItemStack stack) {
		int priority;
		Item item = stack.getItem();

		if (item == Items.NETHERITE_CHESTPLATE) {
			priority = 45;
		}
		else if (item == Items.DIAMOND_CHESTPLATE) {
			priority = 40;
		}
		else if (item == Items.IRON_CHESTPLATE) {
			priority = 35;
		}
		else if (item == Items.GOLDEN_CHESTPLATE) {
			priority = 30;
		}
		else if (item == Items.LEATHER_CHESTPLATE) {
			priority = 25;
		}
		else {
			return -1;
		}

		priority += EnchantmentUtils.getLevel(stack, Enchantments.PROTECTION) * 3;
		priority += EnchantmentUtils.getLevel(stack, Enchantments.BLAST_PROTECTION) * 2;
		priority += EnchantmentUtils.getLevel(stack, Enchantments.MENDING);

		return priority;
	}

	private boolean isChestPlate(Item item) {
		return item == Items.NETHERITE_CHESTPLATE
				|| item == Items.DIAMOND_CHESTPLATE
				|| item == Items.IRON_CHESTPLATE
				|| item == Items.GOLDEN_CHESTPLATE
				|| item == Items.LEATHER_CHESTPLATE;
	}

	public enum Swap {
		ALTERNATIVE,
		SEMI_ALT,
		PICK,
		QUICK_MOVE
	}
}
