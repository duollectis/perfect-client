package com.perfect.client.core.event.events.input;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.utils.input.KeyAction;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public abstract class InputEvent extends Cancellable {

	@Getter
	private final int value;

	@Getter
	private final int modifiers;

	protected final KeyAction action;

	public InputEvent(int value, int modifiers, KeyAction action) {
		this.value = value;
		this.modifiers = modifiers;
		this.action = action;
	}

	public boolean isPress() {
		return action == KeyAction.PRESS;
	}

	public boolean isRelease() {
		return action == KeyAction.RELEASE;
	}

	public boolean isNotPress() {
		return action != KeyAction.PRESS;
	}

	public boolean isNotRelease() {
		return action != KeyAction.RELEASE;
	}

	public boolean isModifiersEmpty() {
		return modifiers == 0;
	}

	public boolean hasModifier(Modifier modifier) {
		return getModifiersList().contains(modifier);
	}

	public List<Modifier> getModifiersList() {
		return KeyEvent.getModifiersList(modifiers);
	}

	public static List<Modifier> getModifiersList(int modCode) {
		List<Modifier> mods = new ArrayList<>();

		for (Modifier mod : Modifier.values()) {
			if ((modCode & (1 << mod.ordinal())) > 0) {
				mods.add(mod);
			}
		}

		return mods;
	}

	public abstract boolean isKey();

	@Getter
	public enum Modifier {
		SHIFT,
		CONTROL,
		ALT,
		SUPER,
		CAPS_LOCK,
		NUM_LOCK
	}
}
