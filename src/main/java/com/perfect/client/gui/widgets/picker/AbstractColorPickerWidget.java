package com.perfect.client.gui.widgets.picker;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.color.IMutableColor;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;

import java.awt.*;

public abstract class AbstractColorPickerWidget extends VerticalListWidget {

	public static final float WIDTH = 60.0f;
	public static final float FIELD_HEIGHT = 36.0f;
	public static final float VERTICAL_INDENT = 1.25f;
	public static final float SLIDER_LINE_HEIGHT = 5.0f;

	protected IMutableColor mutableColor;

	public AbstractColorPickerWidget(IMutableColor mutableColor) {
		this.mutableColor = mutableColor;
	}

	public void setMutableColor(IMutableColor mutableColor) {
		this.mutableColor = mutableColor;

		apply(getSettingColor());
	}

	protected Color getSettingColor() {
		if (mutableColor == null) {
			return Managers.COLOR.get(Managers.COLOR.MAIN);
		}
		return mutableColor.getColor();
	}

	protected void setSettingColor(Color color) {
		if (mutableColor != null) {
			mutableColor.setColor(color);
		}
	}

	protected abstract void apply(Color color);
}
