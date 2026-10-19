package com.perfect.client.gui.widgets.settings.elements.enums.impl;

import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumSetting;
import com.perfect.client.gui.widgets.settings.elements.enums.AbstractEnumWidget;

public class EnumWidget<T extends Enum<T>> extends AbstractEnumWidget<T, T, IEnumSetting<T, T>> {

	public EnumWidget(IEnumSetting<T, T> setting) {
		super(setting);
	}

	@Override
	protected void handleClick(T value) {
		setting.set(value);
	}
}
