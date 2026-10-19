package com.perfect.client.core.event.events.input.keyboard;

import com.perfect.client.core.event.events.input.InputEvent;
import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor

public class CharEvent extends Cancellable {

	private final char character;
	private final int modifiers;

	public List<InputEvent.Modifier> getModifiersList() {
		return InputEvent.getModifiersList(modifiers);
	}
}
