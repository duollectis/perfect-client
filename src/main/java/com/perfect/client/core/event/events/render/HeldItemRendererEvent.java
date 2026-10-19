package com.perfect.client.core.event.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;

@Getter
@AllArgsConstructor

public class HeldItemRendererEvent {

	private final Hand hand;
	private final MatrixStack matrices;
}
