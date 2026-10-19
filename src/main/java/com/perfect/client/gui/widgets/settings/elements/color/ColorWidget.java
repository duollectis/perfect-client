package com.perfect.client.gui.widgets.settings.elements.color;

import com.perfect.client.core.functional.setting.interfaces.settings.IColorSetting;
import com.perfect.client.gui.widgets.picker.color.RGBAColorPickerWidget;
import com.perfect.client.gui.widgets.windows.ColorPickerWindowWidget;

import java.awt.*;

public class ColorWidget extends AbstractColorWidget<Color, IColorSetting> {

	public ColorWidget(IColorSetting setting) {
		super(setting);
	}

	@Override
	protected Color getDrawColor() {
		return setting.get();
	}

	@Override
	protected void openWindow() {
		container.get().addWindow(new ColorPickerWindowWidget(new RGBAColorPickerWidget(setting)));
	}
}
