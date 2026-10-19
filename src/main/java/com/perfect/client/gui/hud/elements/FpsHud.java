package com.perfect.client.gui.hud.elements;

import com.perfect.client.gui.hud.common.AbstractTextHud;

import static com.perfect.client.PerfectClient.mc;

public class FpsHud extends AbstractTextHud {

	public FpsHud() {
		super("fps-hud", "Fps", TextPosition.SUFFIX, "fps", 0.0f, 0.07f);
	}

	@Override
	protected String getValue() {
		return Integer.toString(mc.getCurrentFps());
	}
}
