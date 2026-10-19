package com.perfect.client.core.event.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

@Getter
@AllArgsConstructor

public class Render2DEvent {

	private final DrawContext context;
}
