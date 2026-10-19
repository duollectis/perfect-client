package com.perfect.client.core.event.events.render;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.util.math.MatrixStack;

@Getter
@AllArgsConstructor

public class Render3DEvent {

	private final MatrixStack matrices;
	private final float tickDelta;

}
