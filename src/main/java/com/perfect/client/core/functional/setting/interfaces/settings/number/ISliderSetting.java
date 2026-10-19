package com.perfect.client.core.functional.setting.interfaces.settings.number;

import com.perfect.client.core.functional.setting.interfaces.ISetting;

public interface ISliderSetting<K extends Number, T> extends ISetting<T> {

	K getMin();
	K getMax();
	K getSliderMin();
	K getSliderMax();
	String getSuffix();
}
