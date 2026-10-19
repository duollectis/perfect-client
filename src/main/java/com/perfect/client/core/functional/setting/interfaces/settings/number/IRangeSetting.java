package com.perfect.client.core.functional.setting.interfaces.settings.number;

import com.perfect.client.core.utils.range.AbstractRange;

public interface IRangeSetting<T extends Number, K extends AbstractRange<T>> extends ISliderSetting<T, K> {

	T getFrom();
	T getTo();

	void setFrom(T val);
	void setTo(T val);
}
