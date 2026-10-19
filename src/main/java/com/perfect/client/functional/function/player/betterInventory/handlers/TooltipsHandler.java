package com.perfect.client.functional.function.player.betterInventory.handlers;

import com.perfect.client.core.event.events.render.TooltipRenderEvent;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.game.ItemUtils;
import com.perfect.client.functional.function.player.betterInventory.BetterInventory;
import com.perfect.client.functional.function.player.betterInventory.BetterInventoryConfig;
import com.perfect.client.functional.function.player.betterInventory.tooltips.ContainerTooltipComponent;
import com.perfect.client.functional.function.player.betterInventory.utils.BetterInventoryHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.awt.*;

public class TooltipsHandler extends BetterInventoryHandler {

	private static final ItemStack[] ITEMS = new ItemStack[27];
	public static final Color ECHEST_COLOR = new Color(0, 50, 50);
	public static final Color DEFAULT_SHULKER_COLOR = new Color(148, 103, 148);

	public TooltipsHandler(
			BetterInventory function,
			BetterInventoryConfig config
	) {
		super(function, config);
	}

	@Override
	public void onRenderTooltip(TooltipRenderEvent event) {
		if (isKeyNotPressed()) {
			return;
		}

		ItemStack itemStack = event.getItemStack();

		if (function.getShulkers() && InvUtils.hasItems(itemStack)) {
			ItemUtils.setItemsInContainerToItemStackList(itemStack, ITEMS);
			event.setTooltipData(new ContainerTooltipComponent(ITEMS, ItemUtils.getItemColor(itemStack, DEFAULT_SHULKER_COLOR)));
		}
		else if (itemStack.getItem() == Items.ENDER_CHEST && getEChest()) {
			event.setTooltipData(new ContainerTooltipComponent(
					ItemUtils.eChestMemory.ITEMS.toArray(new ItemStack[27]),
					ECHEST_COLOR
			));
		}
	}

	private boolean getEChest() {
		return config.getTooltips().get() && config.getPreviews()
		                                           .get(BetterInventory.Previews.ENDER_CHEST) && ItemUtils.eChestMemory.isKnown();
	}

	private boolean isKeyNotPressed() {
		return config.getKeybind().isEnabled() && !config.getKeybind().get().isKeyPressed();
	}
}
