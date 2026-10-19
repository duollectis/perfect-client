package com.perfect.client.core.functional.setting.impl;

import com.perfect.client.core.functional.setting.BaseSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.settings.elements.ButtonSettingWidget;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ButtonSetting extends BaseSetting {

	private final Runnable action;

	@Builder
	public ButtonSetting(String name, IVisible visible, Runnable action) {
		super(name, visible);
		this.action = action;
	}

	@Override
	public Widget createWidget() {
		return new ButtonSettingWidget(this::getName, action, this);
	}
}
