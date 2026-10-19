package com.perfect.client.core.functional.setting.interfaces.settings.enums;

import com.perfect.client.core.functional.setting.interfaces.ISetting;

public interface IEnumSetting<T, E extends Enum<?>> extends ISetting<T> {

	E[] getValues();
	boolean get(E value);
}
