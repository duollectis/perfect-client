package com.perfect.client.core.utils.game;

import com.mojang.serialization.DataResult;
import com.perfect.client.functional.function.player.betterInventory.handlers.TooltipsHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.inventory.StackWithSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.collection.DefaultedList;

import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

import static com.perfect.client.PerfectClient.mc;


public class ItemUtils {

	public static EChestMemory eChestMemory = new EChestMemory();

	public static boolean isArmor(ItemStack stack) {
		return stack.isIn(ItemTags.FOOT_ARMOR)
				|| stack.isIn(ItemTags.LEG_ARMOR)
				|| stack.isIn(ItemTags.CHEST_ARMOR)
				|| stack.isIn(ItemTags.HEAD_ARMOR);
	}

	public static boolean isBroken(ItemStack stack) {
		return stack.getDamage() > stack.getMaxDamage();
	}

	public static void setItemsInContainerToItemStackList(ItemStack itemStack, ItemStack[] items) {
		assert mc.player != null;

		if (itemStack.getItem() == Items.ENDER_CHEST) {
			for (int i = 0; i < eChestMemory.ITEMS.size(); i++) {
				items[i] = eChestMemory.ITEMS.get(i);
			}

			return;
		}

		Arrays.fill(items, ItemStack.EMPTY);
		ComponentMap components = itemStack.getComponents();

		if (components == null) {
			return;
		}

		if (components.contains(DataComponentTypes.CONTAINER)) {
			DefaultedList<ItemStack> stacks = components.get(DataComponentTypes.CONTAINER).stacks;

			for (int i = 0; i < stacks.size(); i++) {
				if (i < items.length) {
					items[i] = stacks.get(i);
				}
			}
		}
		else if (components.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) {
			NbtComponent nbt2 = components.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT);
			NbtList nbt3 = nbt2.copyNbt().getListOrEmpty("Items");

			for (int i = 0; i < nbt3.size(); i++) {
				Optional<NbtCompound> compound = nbt3.getCompound(i);
				if (compound.isEmpty()) {
					continue;
				}

				Optional<Byte> slot = compound.get().getByte("Slot");
				if (slot.isEmpty()) {
					continue;
				}

				if (slot.get() >= 0 && slot.get() < items.length) {
					switch (StackWithSlot.CODEC.parse(
							mc.player.getRegistryManager().getOps(NbtOps.INSTANCE),
							compound.get()
					)) {
						case DataResult.Success<StackWithSlot> success -> items[slot.get()] = success.value().stack();
						case DataResult.Error<StackWithSlot> error -> items[slot.get()] = ItemStack.EMPTY;
						default -> throw new MatchException(null, null);
					}
				}
			}
		}
	}

	public static Color getItemColor(ItemStack itemStack, Color defaultColor) {
		if (itemStack.getItem() instanceof BlockItem blockItem) {
			Block block = blockItem.getBlock();
			if (block == Blocks.ENDER_CHEST) {
				return TooltipsHandler.ECHEST_COLOR;
			}

			if (block instanceof ShulkerBoxBlock shulkerBlock) {
				DyeColor dye = shulkerBlock.getColor();
				if (dye == null) {
					return defaultColor;
				}

				final int color = dye.getEntityColor();
				return new Color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, 255);
			}
		}

		return defaultColor;
	}

//	public static boolean hasItems(ItemStack itemStack) {
//		ContainerComponent container = itemStack.get(DataComponentTypes.CONTAINER);
//		if (container != null && !container.stacks.isEmpty()) {
//			return true;
//		}
//
//		NbtCompound compoundTag = itemStack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT)
//		                                   .copyNbt();
//		return compoundTag.contains("Items");
//	}
}
