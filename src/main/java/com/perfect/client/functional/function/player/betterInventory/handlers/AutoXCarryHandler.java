package com.perfect.client.functional.function.player.betterInventory.handlers;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SlotUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.player.betterInventory.BetterInventory;
import com.perfect.client.functional.function.player.betterInventory.BetterInventoryConfig;
import com.perfect.client.functional.function.player.betterInventory.utils.BetterInventoryHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;


public class AutoXCarryHandler extends BetterInventoryHandler {

	private final int[] CRAFT_SLOTS = {1, 2, 3, 4};

	private int timer = 0;

	public AutoXCarryHandler(
			BetterInventory function,
			BetterInventoryConfig config
	) {
		super(function, config);
	}

	
	@Override
	public void onPreTick() {
		timer++;

		if (mc.player == null) {
			return;
		}

		if (mc.player.currentScreenHandler.syncId != mc.player.playerScreenHandler.syncId) {
			return;
		}

		if (timer <= config.getCarryDelay().get()) {
			return;
		}

		int emptyCraftSlot = getEmptyCraftSlot();

		FindItemResult fir = getItemToCarry();

		if (hasItemsToLoot() && fir.found()) {
			if (emptyCraftSlot != 0) {
				InvUtils.clickSlot(SlotUtils.getSlotID(fir.slot()), 0, SlotActionType.PICKUP);
				InvUtils.clickSlot(emptyCraftSlot, 0, SlotActionType.PICKUP);
				timer = 0;
			}
			else if (mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
				InvUtils.clickSlot(SlotUtils.getSlotID(fir.slot()), 0, SlotActionType.PICKUP);
			}
		}
	}

	private FindItemResult getItemToCarry() {
		return InvUtils.findFromEnd(this::isChosenStack, SlotUtils.MAIN_START, SlotUtils.MAIN_END, -1);
	}

	private boolean isChosenStack(ItemStack stack) {
		return config.getItemsToCarry().contains(Managers.ITEM.identifierOf(stack.getItem()));
	}

	private boolean hasItemsToLoot() {
		if (mc.player == null) {
			return false;
		}

		Box searchBox = PositionMath.boxFromCenter(1.0, mc.player.getBoundingBox().getCenter());
		return BlockUtils.hasEntity(searchBox, this::canLoot);
	}

	private boolean canLoot(Entity entity) {
		if (mc.player == null) {
			return false;
		}
		if (!(entity instanceof ItemEntity)) {
			return false;
		}
		if (!entity.isOnGround()) {
			return false;
		}

		Vec3d closest = PositionMath.closestVec3d(entity.getPos(), mc.player.getBoundingBox());
		return !PositionMath.isOutOfRange(entity.getPos(), closest, 0.5);
	}

	private int getEmptyCraftSlot() {
		if (mc.player == null) {
			return 0;
		}

		for (int slot : CRAFT_SLOTS) {
			if (mc.player.playerScreenHandler.getSlot(slot).getStack().isEmpty()) {
				return slot;
			}
		}

		return 0;
	}
}
