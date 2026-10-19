package com.perfect.client.gui.hud.elements.speed;

import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.hud.common.AbstractTextHud;

public class SpeedHud extends AbstractTextHud {

	private final SpeedHudConfig config = new SpeedHudConfig(this);

	public SpeedHud() {
		super("speed-hud", "Speed", TextPosition.SUFFIX, "bps", 0.0f, 0.05f);
	}

	@Override
	public void onPreTick() {
		if (config.getFormatMode().get(FormatMode.BPS)) {
			setText("bps");
		} else {
			setText("km/h");
		}

		super.onPreTick();
	}

	@Override
	protected String getValue() {
		double speed = PlayerUtils.getSpeedInBPS().horizontalLength();

		if (config.getFormatMode().get(FormatMode.KMH)) {
			speed *= 3.6;
		}

		return StringUtils.formatDoubles(speed, 1);
	}

	public void updateInfo() {
		setCachedTextWidth(font.getTextWidth(text));
	}

	public enum FormatMode {
		BPS,
		KMH
	}
}