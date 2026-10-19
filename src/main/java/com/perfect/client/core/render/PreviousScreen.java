package com.perfect.client.core.render;

import com.perfect.client.core.event.events.render.ScreenChangedEvent;
import com.perfect.client.core.event.system.EventListener;
import net.minecraft.client.gui.screen.Screen;

import static com.perfect.client.PerfectClient.mc;

public class PreviousScreen {

	private static Screen previous = null;

	public static void init() {
		new PreviousScreenEventListener();
	}

	public static Screen get() {
		return previous;
	}

	public static void set() {
		mc.setScreen(previous);
	}

	public static class PreviousScreenEventListener extends EventListener {

		@Override
		public void onScreenChanged(ScreenChangedEvent event) {
			previous = event.getPreviousScreen();
		}
	}
}
