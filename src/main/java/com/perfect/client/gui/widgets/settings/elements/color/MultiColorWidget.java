package com.perfect.client.gui.widgets.settings.elements.color;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.gui.widgets.picker.multi.MultiColorPickerWidget;
import com.perfect.client.gui.widgets.windows.ColorPickerWindowWidget;

import java.awt.*;

public class MultiColorWidget extends AbstractColorWidget<MultiColor, ISetting<MultiColor>> {

	public MultiColorWidget(ISetting<MultiColor> setting) {
		super(setting);
	}

	@Override
	protected Color getDrawColor() {
		return setting.get().getColor();
	}

	@Override
	protected void openWindow() {
		container.get().addWindow(new ColorPickerWindowWidget(new MultiColorPickerWidget(setting)));
	}
}
