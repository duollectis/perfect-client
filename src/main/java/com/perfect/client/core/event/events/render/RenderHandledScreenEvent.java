package com.perfect.client.core.event.events.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.screen.ScreenHandler;

@Getter
@RequiredArgsConstructor

public class RenderHandledScreenEvent {
	private final DrawContext context;
	private final ScreenHandler handler;

	private final int containerX, containerY;
}