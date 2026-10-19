package com.perfect.client.gui.widgets.settings.elements;

import com.perfect.client.core.functional.setting.impl.ButtonSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.gui.widgets.common.TextWidget;
import com.perfect.client.gui.widgets.common.button.ButtonWidget;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class ButtonSettingWidget extends ButtonWidget implements IVisible {

	private final ButtonSetting buttonSetting;

	public ButtonSettingWidget(Supplier<String> textSupplier, Runnable action, ButtonSetting buttonSetting) {
		super(action);
		this.buttonSetting = buttonSetting;

		add(new TextWidget(textSupplier));
	}

	@Override
	public boolean isVisible() {
		return buttonSetting.isVisible();
	}
}
