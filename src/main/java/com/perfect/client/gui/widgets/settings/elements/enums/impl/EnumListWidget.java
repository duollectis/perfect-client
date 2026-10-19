package com.perfect.client.gui.widgets.settings.elements.enums.impl;

import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumListSetting;
import com.perfect.client.gui.widgets.settings.elements.enums.AbstractEnumWidget;

import java.util.Set;

public class EnumListWidget<T extends Enum<?>> extends AbstractEnumWidget<Set<T>, T, IEnumListSetting<Set<T>, T>> {

	public EnumListWidget(IEnumListSetting<Set<T>, T> setting) {
		super(setting);
	}

	@Override
	protected void handleClick(T value) {
		if (setting.get(value)) {
			setting.remove(value);
		}
		else {
			setting.add(value);
		}
	}
}
