package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.gui.hud.common.AbstractTextHud;

public class PingHud extends AbstractTextHud {

	public PingHud() {
		super("ping-hud", "Ping", TextPosition.SUFFIX, "ping", 0.0f, 0.07f);
	}

	@Override
	protected String getValue() {
		return Integer.toString(Managers.PING.getPing());
	}
}
