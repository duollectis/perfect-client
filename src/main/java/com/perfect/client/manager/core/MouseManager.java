package com.perfect.client.manager.core;

import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.manager.Manager;
import lombok.Getter;

@Getter
public class MouseManager extends Manager {

	private float x;
	private float y;

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		x = event.getX();
		y = event.getY();
	}

	@Override
	public void onStartRenderTick() {
		double scale = mc.getWindow().getScaleFactor();

		x = (float) (mc.mouse.getX() / scale);
		y = (float) (mc.mouse.getY() / scale);
	}
}
