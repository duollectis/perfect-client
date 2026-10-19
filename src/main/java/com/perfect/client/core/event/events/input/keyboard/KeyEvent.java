package com.perfect.client.core.event.events.input.keyboard;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.utils.input.KeyAction;


public class KeyEvent extends InputEvent {

	public KeyEvent(int key, int modifiers, KeyAction action) {
		super(key, modifiers, action);
	}

	@Override
	public boolean isKey() {
		return true;
	}

	public boolean isRepeat() {
		return action == KeyAction.REPEAT;
	}

	public boolean isNotRepeat() {
		return action != KeyAction.REPEAT;
	}
}
