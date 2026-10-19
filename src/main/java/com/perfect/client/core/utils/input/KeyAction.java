package com.perfect.client.core.utils.input;

import org.lwjgl.glfw.GLFW;

public enum KeyAction {
	PRESS,
	REPEAT,
	RELEASE;

	public static KeyAction get(int action) {
		return switch (action) {
			case GLFW.GLFW_PRESS -> PRESS;
			case GLFW.GLFW_RELEASE -> RELEASE;
			default -> REPEAT;
		};
	}
}
