package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.gui.hud.common.AbstractTextHud;

public class CoordinatesHud extends AbstractTextHud {
	public CoordinatesHud() {
		super("coordinates-hud", "Coordinates", TextPosition.PREFIX, "XYZ", 0.0f, 0.98f);
	}

	@Override
	protected String getValue() {
		return PlayerUtils.getFormattedCoordinates();
	}
}
