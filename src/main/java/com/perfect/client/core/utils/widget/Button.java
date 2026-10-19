package com.perfect.client.core.utils.widget;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import lombok.Getter;
import lombok.Setter;


public class Button {

	private static final float MAX_MOVEMENT = 5f;

	@Getter
	@Setter
	private boolean hovered;
	private boolean pressHovered;

	private float pressX;
	private float pressY;

	public boolean isClick(MouseButtonEvent event, float mouseX, float mouseY) {
		if (event.isNotLeft() && event.isNotRight()) {
			return false;
		}

		return isAnyClick(event, mouseX, mouseY);
	}

	public boolean isAnyClick(MouseButtonEvent event, float mouseX, float mouseY) {
		if (event.isRelease()) {
			return pressHovered && hovered && !isMoved(mouseX, mouseY);
		}

		if (event.isPress()) {
			pressHovered = hovered;

			pressX = mouseX;
			pressY = mouseY;
		}

		return false;
	}

	private boolean isMoved(float mouseX, float mouseY) {
		float diffX = Math.abs(pressX - mouseX);
		float diffY = Math.abs(pressY - mouseY);

		return diffX > MAX_MOVEMENT || diffY > MAX_MOVEMENT;
	}

	public void unPress() {
		pressHovered = false;
	}
}

