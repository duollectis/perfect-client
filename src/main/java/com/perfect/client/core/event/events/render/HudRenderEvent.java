package com.perfect.client.core.event.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

@Getter
@AllArgsConstructor

public class HudRenderEvent {

	private final DrawContext context;
	private final RenderTickCounter tickCounter;
}
