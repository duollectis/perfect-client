package com.perfect.client.functional.function.player.betterInventory;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ToggleContainer;
import com.perfect.client.functional.function.player.betterInventory.handlers.AutoXCarryHandler;
import com.perfect.client.functional.function.player.betterInventory.handlers.TooltipsHandler;
import com.perfect.client.functional.function.player.betterInventory.handlers.XCarryHandler;
import com.perfect.client.functional.function.player.betterInventory.utils.BetterInventoryHandler;

public class BetterInventory extends Function {

	private final BetterInventoryConfig config = new BetterInventoryConfig(this);

	private final ToggleContainer<BetterInventoryHandler> toggleContainer = createToggleHandler()
			.add(config.getXCarry(), new XCarryHandler(this, config))
			.add(config.getAutoXCarry(), new AutoXCarryHandler(this, config))
			.add(config.getTooltips(), new TooltipsHandler(this, config));

	public boolean updateTooltips = false;

	public BetterInventory() {
		super("better-inventory", "Better Inventory", Category.PLAYER, "Inventory Utils", "Inventory Tweaks");
	}

	public void onToggled() {
		toggleContainer.onToggled();
	}

	public boolean updateTooltips() {
		if (updateTooltips && isEnabled()) {
			updateTooltips = false;
			return true;
		}

		return false;
	}

	public boolean getShulkers() {
		return isEnabled() && config.getTooltips().get() && config.getPreviews().get(BetterInventory.Previews.SHULKERS);
	}

	public boolean shouldCancelTooltip() {
		if (!config.getKeybind().isEnabled()) {
			return true;
		}

		return config.getKeybind().get().isKeyPressed();
	}

	public enum Previews {
		SHULKERS,
		ENDER_CHEST
	}
}
