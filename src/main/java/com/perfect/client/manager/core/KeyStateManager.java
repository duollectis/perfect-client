package com.perfect.client.manager.core;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.input.Binds;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class KeyStateManager extends Manager {

	private final boolean[] keyboard = new boolean[512];
	private final boolean[] mouse = new boolean[16];

	@Override
	public void onGlobalKey(KeyEvent event) {
		if (event.isNotRepeat()) {
			setKeyboardState(event.getValue(), event.isPress());
		}
	}

	@Override
	public void onGlobalMouseButton(MouseButtonEvent event) {
		setMouseState(event.getValue(), event.isPress());
	}

	public boolean isLeftPressed() {
		return isMousePressed(GLFW.GLFW_MOUSE_BUTTON_LEFT);
	}

	public boolean isRightPressed() {
		return isMousePressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT);
	}

	public void setKeyboardState(KeyBinding bind, boolean pressed) {
		setKeyboardState(Binds.getKey(bind), pressed);
	}

	public void setKeyboardState(int key, boolean pressed) {
		if (key >= 0 && key < keyboard.length) {
			keyboard[key] = pressed;
		}
	}

	public void setMouseState(int button, boolean pressed) {
		if (button >= 0 && button < mouse.length) {
			mouse[button] = pressed;
		}
	}

	public boolean isAnyMousePressed() {
		for (boolean state : mouse) {
			if (state) {
				return true;
			}
		}

		return false;
	}

	public boolean isKeyboardPressed(int key) {
		if (key == GLFW.GLFW_KEY_UNKNOWN) {
			return false;
		}

		return key < keyboard.length && keyboard[key];
	}

	public boolean isMousePressed(int button) {
		if (button == -1) {
			return false;
		}

		return button < mouse.length && mouse[button];
	}

	public List<Integer> collectPressedKeyboard() {
		List<Integer> pressed = new ArrayList<>();

		for (int key = 0; key < keyboard.length; key++) {
			if (keyboard[key]) {
				pressed.add(key);
			}
		}

		return pressed;
	}
}
