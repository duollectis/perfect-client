package com.perfect.client.core.functional.setting.interfaces.settings.number;

public interface INumberSetting<T extends Number> extends ISliderSetting<T, T> {

	void setWidgetDragging(boolean dragging);
	boolean isWidgetDragging();
}
