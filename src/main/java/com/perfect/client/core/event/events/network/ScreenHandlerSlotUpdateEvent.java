package com.perfect.client.core.event.events.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.item.ItemStack;

@Getter
@AllArgsConstructor

public class ScreenHandlerSlotUpdateEvent {

	private final int syncId;
	private final int revision;
	private final int slot;
	private final ItemStack stack;
}
