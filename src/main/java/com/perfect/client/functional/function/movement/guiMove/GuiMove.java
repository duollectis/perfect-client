package com.perfect.client.functional.function.movement.guiMove;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.render.ScreenChangedEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.gui.widgets.ClickGuiScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;

public class GuiMove extends Function {

	public GuiMove() {
		super("gui-move", "Gui Move", Category.MOVEMENT);
	}

	@Override
	public void onScreenChanged(ScreenChangedEvent event) {
		mc.options.useKey.setPressed(false);
	}

	@Override
	public void onKey(KeyEvent keyEvent) {
		if (!isGoodScreen() || keyEvent.isRepeat()) {
			return;
		}

		boolean isPress = keyEvent.isPress();
		int value = keyEvent.getValue();

		KeyBinding[] keyBindings = new KeyBinding[] {
				mc.options.forwardKey,
				mc.options.backKey,
				mc.options.leftKey,
				mc.options.rightKey,
				mc.options.jumpKey,
				mc.options.sneakKey
		};

		for (KeyBinding keyBinding : keyBindings) {
			if (value == keyBinding.boundKey.getCode()) {
				pressUnpressKey(keyBinding, isPress);
				break;
			}
		}
	}

	private void pressUnpressKey(KeyBinding key, boolean pressed) {
		key.setPressed(pressed);
	}

	public boolean shouldWork() {
		return isEnabled() && isGoodScreen();
	}

	private boolean isGoodScreen() {
		return mc.currentScreen == null
				|| mc.currentScreen instanceof ClickGuiScreen
				|| mc.currentScreen instanceof InventoryScreen
				|| mc.currentScreen instanceof CreativeInventoryScreen
				|| mc.currentScreen instanceof GenericContainerScreen;
	}
}
