package com.perfect.client.core.event.events.input.keyboard;

import java.util.ArrayList;
import java.util.List;


public class MovementInputEvent {

	private final List<Input> inputs = new ArrayList<>();

	public Input forward = addInput();
	public Input back = addInput();
	public Input right = addInput();
	public Input left = addInput();

	public Input sneaking = addInput();
	public Input jumping = addInput();
	public Input sprinting = addInput();

	private Input addInput() {
		Input input = new Input();
		inputs.add(input);
		return input;
	}

	public void setAllInput(boolean val) {
		inputs.forEach(input -> input.set(val));
	}

	public static class Input {

		private InputState inputState = InputState.NOT_SET;

		public boolean isSet() {
			return inputState != InputState.NOT_SET;
		}

		public void set(boolean val) {
			if (val) {
				inputState = InputState.TRUE;
			}
			else {
				inputState = InputState.FALSE;
			}
		}

		public boolean getOrDefault(boolean def) {
			if (inputState == InputState.NOT_SET) {
				return def;
			}
			return inputState == InputState.TRUE;
		}
	}

	private enum InputState {
		TRUE,
		FALSE,
		NOT_SET;
	}
}
