package com.perfect.client.core.event.events.render;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;

@Getter
@AllArgsConstructor

public class ScreenChangedEvent extends Cancellable {

	private final Screen newScreen;
	private final Screen previousScreen;
}
