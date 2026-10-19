package com.perfect.client.gui.hud.elements;

import com.perfect.client.Entry;
import com.perfect.client.gui.hud.common.AbstractTextHud;

public class WatermarkHud extends AbstractTextHud {

	private static final String VALUE = "v" + Entry.getCLIENT_VERSION();

	public WatermarkHud() {
		super("watermark-hud", "Watermark", TextPosition.PREFIX, "Perfect Client", 0.0f, 0.0f);
	}

	@Override
	protected String getValue() {
		return VALUE;
	}
}