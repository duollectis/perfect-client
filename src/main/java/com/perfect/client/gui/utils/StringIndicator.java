package com.perfect.client.gui.utils;

import com.perfect.client.core.utils.math.TimerUtils;

public class StringIndicator {

	private final TimerUtils timer = new TimerUtils();
	private boolean indicator;

	public StringIndicator() {
		reset();
	}

	public String get() {
		if (timer.passedMillis(450)) {
			indicator = !indicator;
			timer.reset();
		}

		return indicator ? "|" : "";
	}

	public void reset() {
		timer.reset();
		indicator = true;
	}
}