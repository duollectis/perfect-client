package com.perfect.client.functional.function.player.betterInventory.utils;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;

public interface ITooltip extends TooltipData {
	TooltipComponent getComponent();
}