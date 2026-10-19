package com.perfect.client.core.utils.widget;

import com.perfect.client.core.manager.Managers;
import lombok.Setter;

import java.util.function.Consumer;


public class Dragging {

	private int button = -1;

	@Setter
	private Consumer<Boolean> onSwitched;

	public void press(int button) {
		this.button = button;

		if (onSwitched != null) {
			onSwitched.accept(true);
		}
	}

	public boolean isDragging() {
		return button != -1;
	}

	public void unPress() {
		if (onSwitched != null) {
			onSwitched.accept(false);
		}

		button = -1;
	}

	public boolean checkNeedUnPress() {
		if (!isDragging()) {
			return false;
		}

		if (!Managers.KEY_STATE.isMousePressed(button)) {
			unPress();
			return true;
		}

		return false;
	}
}
