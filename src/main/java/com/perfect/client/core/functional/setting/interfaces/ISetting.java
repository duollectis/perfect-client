package com.perfect.client.core.functional.setting.interfaces;

import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;

public interface ISetting<T> extends IBaseSetting {

	T get();
	T getDefaultValue();
	boolean set(T value);
	void reset();
	IBoolSetting getToggleable();
}
