package com.perfect.client.core.functional.setting.interfaces.settings.enums;

public interface IEnumListSetting<T, E extends Enum<?>> extends IEnumSetting<T, E> {

	void add(E value);
	void remove(E value);
}
