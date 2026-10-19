package com.perfect.client.functional.function.player.autoRespawn;

import com.perfect.client.core.event.events.render.ScreenChangedEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Function {

	public AutoRespawn() {
		super("auto-respawn", "Auto Respawn", Category.PLAYER);
	}

	@Override
	public void onAutoRespawnScreenChanged(ScreenChangedEvent event) {
		if (mc.player == null) {
			return;
		}

		if (event.getNewScreen() instanceof DeathScreen) {
			mc.player.requestRespawn();
			event.cancel();
		}
	}
}
