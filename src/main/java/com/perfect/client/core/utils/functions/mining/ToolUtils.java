package com.perfect.client.core.utils.functions.mining;

import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.functions.inventory.Swap;
import net.minecraft.block.BambooBlock;
import net.minecraft.block.BambooShootBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.functions.inventory.EnchantmentUtils.getLevel;

public class ToolUtils {

	public static FindItemResult getBestTool(BlockState state, Enchantment preference, Swap swap) {
		return getBestTool(state, preference, 0, getSearchEnd(swap));
	}

	private static int getSearchEnd(Swap swap) {
		if (mc.player == null) {
			return -1;
		}

		if (swap == Swap.HOTBAR) {
			return SlotUtils.HOTBAR_END;
		}
		return mc.player.getInventory().size();
	}

	public static FindItemResult getBestTool(BlockState state, Enchantment preference, int start, int end) {
		if (mc.player == null) {
			return FindItemResult.NOT_FOUND;
		}

		float bestScore = -1f;

		ItemStack bestStack = null;
		int bestSlot = -1;

		for (int i = start; i <= end; i++) {
			ItemStack stack = mc.player.getInventory().getStack(i);

			if (!isGoodTool(stack, state)) {
				continue;
			}

			float score = getToolScore(stack, state, preference);

			if (score > bestScore) {
				bestScore = score;

				bestSlot = i;
				bestStack = stack;
			}
		}

		return new FindItemResult(bestStack, bestSlot);
	}

	private static float getToolScore(ItemStack stack, BlockState state, Enchantment preference) {
		float score = 0;

		score += stack.getMiningSpeedMultiplier(state) * 1000f;
		score += getLevel(stack, Enchantments.UNBREAKING);
		score += getLevel(stack, Enchantments.EFFICIENCY);
		score += getLevel(stack, Enchantments.MENDING);

		switch (preference) {
			case FORTUNE -> score += getLevel(stack, Enchantments.FORTUNE) * 3;
			case SILK_TOUCH -> score += getLevel(stack, Enchantments.SILK_TOUCH) * 3;
		}

		return score;
	}

	private static boolean isGoodTool(ItemStack stack, BlockState state) {
		if (!isTool(stack)) {
			return false;
		}
		if (stack.getItem() instanceof ShearsItem && isSuitableForShears(state)) {
			return true;
		}
		if (stack.isIn(ItemTags.SWORDS) && isSuitableForSword(state)) {
			return true;
		}
		return stack.isSuitableFor(state);
	}

	private static boolean isSuitableForSword(BlockState state) {
		return state.getBlock() instanceof BambooBlock || state.getBlock() instanceof BambooShootBlock;
	}

	private static boolean isSuitableForShears(BlockState state) {
		return state.getBlock() instanceof LeavesBlock || state.isIn(BlockTags.WOOL);
	}

	private static boolean isTool(ItemStack stack) {
		return stack.isIn(ItemTags.AXES)
				|| stack.isIn(ItemTags.HOES)
				|| stack.isIn(ItemTags.PICKAXES)
				|| stack.isIn(ItemTags.SHOVELS)
				|| stack.getItem() instanceof ShearsItem;
	}

	public enum Enchantment {
		FORTUNE,
		SILK_TOUCH
	}
}
