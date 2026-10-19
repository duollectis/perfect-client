package com.perfect.client.core.event.events.input.mouse;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.utils.input.KeyAction;
import org.lwjgl.glfw.GLFW;


public class MouseButtonEvent extends InputEvent {

	public MouseButtonEvent(int key, int modifiers, KeyAction action) {
		super(key, modifiers, action);
	}

	@Override
	public boolean isKey() {
		return false;
	}

	public boolean isLeft() {
		return getValue() == GLFW.GLFW_MOUSE_BUTTON_LEFT;
	}

	public boolean isRight() {
		return getValue() == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	}

	public boolean isNotLeft() {
		return getValue() != GLFW.GLFW_MOUSE_BUTTON_LEFT;
	}

	public boolean isNotRight() {
		return getValue() != GLFW.GLFW_MOUSE_BUTTON_RIGHT;
	}

	public boolean isLeftOrRightPress() {
		return isLeftPress() || isRightPress();
	}

	public boolean isLeftPress() {
		return isPress() && isLeft();
	}

	public boolean isRightPress() {
		return isPress() && isRight();
	}
}
