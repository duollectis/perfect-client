package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.gui.hud.common.AbstractTextHud;

public class TpsHud extends AbstractTextHud {
	public TpsHud() {
		super("tps-hud", "Tps", TextPosition.SUFFIX, "tps", 0.0f, 0.024f);
	}

	@Override
	protected String getValue() {
		return Float.toString(Managers.TICKRATE.getTps());
	}
}