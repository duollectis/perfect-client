package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.hud.common.AbstractTextHud;

import static com.perfect.client.PerfectClient.mc;

public class WelcomerHud extends AbstractTextHud {
	private static final String[] welcomeMessages = {
			"Welcome to the chaos, %s!",
			"Hello %s :')",
			"Watch your back, %s...",
			"Look who's here, it's %s!",
			"Stay safe, %s (or don't).",
			"What's up, %s?",
			"Welcome, %s!",
			"Still playing this dead game, %s?",
			"Hey %s, remember to turn on your Auras.",
			"Good luck, %s."
	};

	public WelcomerHud() {
		super("welcomer-hud", "Welcomer", TextPosition.ONLY_PREFIX, welcomeMessages[MathUtils.random(0, welcomeMessages.length)].formatted(mc.getGameProfile().getName()), 0.5f, 1.0f);
	}

	@Override
	protected String getValue() {
		return "";
	}
}
