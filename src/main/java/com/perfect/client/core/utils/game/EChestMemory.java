package com.perfect.client.core.utils.game;

import com.perfect.client.core.event.events.block.BlockUpdateEvent;
import com.perfect.client.core.event.events.render.ScreenChangedEvent;
import com.perfect.client.core.event.system.EventListener;
import lombok.Getter;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.util.collection.DefaultedList;

import static com.perfect.client.PerfectClient.mc;

public class EChestMemory extends EventListener {
	public final DefaultedList<ItemStack> ITEMS = DefaultedList.ofSize(27, ItemStack.EMPTY);
	private int echestOpenedState;

	@Getter
	private boolean isKnown = false;

	@Override
	public void onBlockInteract(BlockUpdateEvent event) {
		if (event.getNewState().getBlock() instanceof EnderChestBlock && echestOpenedState == 0) {
			echestOpenedState = 1;
		}
	}

	@Override
	public void onScreenChanged(ScreenChangedEvent event) {
		if (echestOpenedState == 1 && event.getNewScreen() instanceof GenericContainerScreen) {
			echestOpenedState = 2;
			return;
		}
		if (echestOpenedState == 0) {
			return;
		}

		if (!(mc.currentScreen instanceof GenericContainerScreen)) {
			return;
		}

		GenericContainerScreenHandler container = ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
		if (container == null) {
			return;
		}

		Inventory inv = container.getInventory();

		for (int i = 0; i < 27; i++) {
			ITEMS.set(i, inv.getStack(i));
		}

		isKnown = true;

		echestOpenedState = 0;
	}

	@Override
	public void onGameLeft() {
		ITEMS.clear();
		isKnown = false;
	}
}