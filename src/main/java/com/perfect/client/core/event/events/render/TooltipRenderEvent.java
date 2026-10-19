package com.perfect.client.core.event.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

@Getter
@Setter
@AllArgsConstructor

public class TooltipRenderEvent {

	private TooltipData tooltipData;
	private ItemStack itemStack;
}
