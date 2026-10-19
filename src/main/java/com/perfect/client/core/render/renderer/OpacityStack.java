package com.perfect.client.core.render.renderer;

import java.util.Stack;


public class OpacityStack {

	private final static Stack<Float> STACK = new Stack<>();

	public static void push(float multiplier) {
		if (STACK.isEmpty()) {
			applyOpacity(STACK.push(multiplier));
			return;
		}

		float opacity = STACK.peek() * multiplier;
		applyOpacity(STACK.push(opacity));
	}

	public static void pop() {
		STACK.pop();

		if (STACK.isEmpty()) {
			applyOpacity(1f);
		}
		else {
			applyOpacity(STACK.peek());
		}
	}

	private static void applyOpacity(float opacity) {
		Renderer2D.setDrawOpacity(opacity);
	}
}
