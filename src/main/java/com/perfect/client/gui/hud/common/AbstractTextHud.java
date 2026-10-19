package com.perfect.client.gui.hud.common;

import com.perfect.client.gui.hud.base.HudWidget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.hudSettings;

public abstract class AbstractTextHud extends HudWidget {

	private static final float DEPTH = 1.0f;

	private String lastValue = null;
	private String value = "";
	private float valueWidth = 0.0f;

	@Setter
	private float cachedTextWidth = -1.0f;

	private final TextPosition textPosition;

	@Setter
	protected String text;

	public AbstractTextHud(
			String id,
			String name,
			TextPosition textPosition,
			String text,
			float defaultX,
			float defaultY
	) {
		super(id, name, defaultX, defaultY);
		this.textPosition = textPosition;
		this.text = text;
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		String newValue = getValue();
		if (newValue == null) {
			newValue = "";
		}

		if (!newValue.equals(lastValue)) {
			value = newValue;
			lastValue = newValue;
			valueWidth = font.getTextWidth(value);

			if (cachedTextWidth == -1.0f) {
				cachedTextWidth = font.getTextWidth(text);
			}
		}
	}

	protected abstract String getValue();

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);

		width = valueWidth + cachedTextWidth + DEPTH + EXPAND * 2.0f;
		height = font.getFontHeight() + EXPAND * 2.0f;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		drawBackground(matrices);

		float spread = hudSettings.getColorSpread().getFloat() / 1000f;
		float halfHeight = height / 2.0f;

		switch (textPosition) {
			case SUFFIX -> {
				font.drawYCentered(
						matrices,
						value,
						drawX + EXPAND,
						drawY + halfHeight,
						hudSettings.getValueTextColor().get()
				);

				font.drawYCentered(
						matrices,
						text,
						drawX + valueWidth + EXPAND + DEPTH,
						drawY + halfHeight,
						hudSettings.getTextColor().get(),
						value.length() * spread,
						spread
				);
			}
			case PREFIX -> {
				font.drawYCentered(
						matrices,
						text,
						drawX + EXPAND,
						drawY + halfHeight,
						hudSettings.getTextColor().get(),
						0f,
						spread
				);

				font.drawYCentered(
						matrices,
						value,
						drawX + width - EXPAND - valueWidth,
						drawY + halfHeight,
						hudSettings.getValueTextColor().get()
				);
			}
			case ONLY_PREFIX -> {
				font.drawYCentered(
						matrices,
						text,
						drawX + EXPAND,
						drawY + halfHeight,
						hudSettings.getTextColor().get(),
						0f,
						spread
				);
			}
		}
	}

	public enum TextPosition {
		PREFIX,
		SUFFIX,
		ONLY_PREFIX
	}
}