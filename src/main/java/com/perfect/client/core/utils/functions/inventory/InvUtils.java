package com.perfect.client.core.utils.functions.inventory;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.inject.mixinInterface.IClientPlayerInteractionManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.function.Predicate;

import static com.perfect.client.PerfectClient.mc;


public class InvUtils {

	private static int previousSlot = -1;

	public static ItemStack getStack(int slot) {
		if (mc.player == null || !PacketUtils.isNormalSlot(slot)) {
			return ItemStack.EMPTY;
		}

		return mc.player.getInventory().getStack(slot);
	}

	public static boolean hasItems(ItemStack itemStack) {
		ContainerComponent compoundTag = itemStack.get(DataComponentTypes.CONTAINER);
		return compoundTag != null && !compoundTag.stream().toList().isEmpty();
	}

	public static void setSlot(int slot, boolean swapBack) {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (!canSwap(slot)) {
			return;
		}

		setPrevSlot(swapBack);

		mc.player.getInventory().setSelectedSlot(slot);

		if (!Managers.NETWORK.isHolding(slot)) {
			((IClientPlayerInteractionManager) mc.interactionManager).perfect$syncSelectedSlot();
		}
	}

	private static void setPrevSlot(boolean swapBack) {
		if (mc.player == null) {
			return;
		}

		if (swapBack && previousSlot == -1) {
			previousSlot = mc.player.getInventory().getSelectedSlot();
		}

		else if (!swapBack) {
			previousSlot = -1;
		}
	}

	private static boolean canSwap(int slot) {
		if (slot == SlotUtils.OFFHAND) {
			return false;
		}

		return slot >= 0 && slot <= 8;
	}

	public static int getFirstHotbarSlotId() {
		if (mc.player == null) {
			return 0;
		}

		if (mc.player.currentScreenHandler instanceof PlayerScreenHandler) {
			return 36;
		}

		return mc.player.currentScreenHandler.slots.size() - 9;
	}

	public static void move(int id, int slot) {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (id == slot) {
			return;
		}

		if (id == -1 || slot == -1) {
			return;
		}

		clickSlot(id, slot, SlotActionType.SWAP);
	}

	
	public static FindItemResult findEmpty() {
		return find(ItemStack::isEmpty);
	}

	
	public static FindItemResult findInHotbar(Item... items) {
		return findInHotbar(itemStack -> {
			for (Item item : items) {
				if (itemStack.getItem() == item) {
					return true;
				}
			}

			return false;
		});
	}

	
	public static FindItemResult findInHotbar(Predicate<ItemStack> isGood) {
		return findInHotbar(true, isGood);
	}

	
	public static FindItemResult findEmptyInHotbar() {
		return findInHotbar(false, ItemStack::isEmpty);
	}

	
	public static FindItemResult findInHotbar(boolean offhand, Predicate<ItemStack> isGood) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		if (offhand && isGood.test(mc.player.getOffHandStack())) {
			return new FindItemResult(
					mc.player.getOffHandStack(),
					SlotUtils.OFFHAND
			);
		}

		if (isGood.test(mc.player.getMainHandStack())) {
			return new FindItemResult(
					mc.player.getMainHandStack(),
					mc.player.getInventory().getSelectedSlot()
			);
		}

		return find(isGood, 0, 8);
	}

	
	public static FindItemResult find(Item... items) {
		return find(itemStack -> {
			for (Item item : items) {
				if (itemStack.getItem() == item) {
					return true;
				}
			}

			return false;
		});
	}

	
	public static FindItemResult find(Predicate<ItemStack> isGood) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		return find(isGood, 0, mc.player.getInventory().size());
	}

	
	public static FindItemResult findFromEnd(Predicate<ItemStack> isGood) {
		return findFromEnd(isGood, -1);
	}

	
	public static FindItemResult findFromEnd(Predicate<ItemStack> isGood, int prioritySlot) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		return findFromEnd(isGood, 0, mc.player.getInventory().size(), prioritySlot);
	}

	
	public static FindItemResult findFromEnd(Predicate<ItemStack> isGood, int start, int end, int prioritySlot) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		int slot = -1;
		ItemStack foundStack = null;

		for (int i = end; i >= start; i--) {
			ItemStack stack = getStack(i);

			if (isGood.test(stack)) {
				boolean priority = prioritySlot != -1 && prioritySlot == slot;

				slot = i;
				foundStack = stack;

				if (prioritySlot == -1 || priority) {
					break;
				}
			}
		}

		return new FindItemResult(foundStack, slot);
	}

	
	public static FindItemResult find(Predicate<ItemStack> isGood, int start, int end) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		int slot = -1;
		ItemStack foundStack = null;

		for (int i = start; i <= end; i++) {
			ItemStack stack = getStack(i);

			if (isGood.test(stack)) {
				slot = i;
				foundStack = stack;
				break;
			}
		}

		return new FindItemResult(foundStack, slot);
	}

	public static void swapBack() {
		if (previousSlot == -1) {
			return;
		}

		setSlot(previousSlot, false);
		previousSlot = -1;
	}

	
	public static double getDurabilityPercentage(ItemStack stack) {
		return 100.0f * (double) (stack.getMaxDamage() - stack.getDamage()) / (double) stack.getMaxDamage();
	}

	public static void clickSlot(int slotId, int button, SlotActionType actionType) {
		if (mc.player == null || mc.interactionManager == null) {
			return;
		}

		if (slotId < 0) {
			return;
		}

		mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotId, button, actionType, mc.player);
	}

	public static void clickSlotSilent(int slotId, int button, SlotActionType actionType) {
		if (mc.player == null || slotId < 0) {
			return;
		}

		ScreenHandler handler = mc.player.currentScreenHandler;

		DefaultedList<Slot> defaultedList = handler.slots;
		int i = defaultedList.size();

		List<ItemStack> list = Lists.newArrayListWithCapacity(i);

		for (Slot slot : defaultedList) {
			list.add(slot.getStack().copy());
		}

		Int2ObjectMap<ItemStackHash> int2ObjectMap = new Int2ObjectOpenHashMap<>();

		for (int j = 0; j < i; ++j) {
			ItemStack stack = list.get(j);
			ItemStack hashItemStack = (defaultedList.get(j)).getStack();
			if (!ItemStack.areEqual(stack, hashItemStack)) {
				ItemStackHash hash = ItemStackHash.fromItemStack(
						hashItemStack,
						mc.player.networkHandler.getComponentHasher()
				);
				int2ObjectMap.put(j, hash);
			}
		}

		ItemStackHash itemStackHash = ItemStackHash.fromItemStack(
				handler.getCursorStack(),
				mc.player.networkHandler.getComponentHasher()
		);

		PacketUtils.sendPacket(new ClickSlotC2SPacket(
				handler.syncId,
				handler.getRevision(),
				Shorts.checkedCast(slotId),
				SignedBytes.checkedCast(button),
				actionType,
				int2ObjectMap,
				itemStackHash
		));
	}

	public static void useItem(Hand hand, float yaw, float pitch) {
		PacketUtils.sendSequencedPacket(creator -> new PlayerInteractItemC2SPacket(hand, creator, yaw, pitch));
	}
}