package com.perfect.client.gui;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;

public class DrawConstants {

	@Getter
	private static float screenWidth;
	@Getter
	private static float screenHeight;
	@Getter
	private static float centerX;
	@Getter
	private static float centerY;

	public static void updateConstants(DrawContext context) {
		screenWidth = context.getScaledWindowWidth();
		screenHeight = context.getScaledWindowHeight();

		centerX = screenWidth / 2f;
		centerY = screenHeight / 2f;
	}
}
