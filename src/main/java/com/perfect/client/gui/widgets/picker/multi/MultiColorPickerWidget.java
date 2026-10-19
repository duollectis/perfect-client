package com.perfect.client.gui.widgets.picker.multi;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.scrollable.HorizontalListWidget;
import com.perfect.client.gui.widgets.picker.AbstractColorPickerWidget;
import com.perfect.client.gui.widgets.picker.multi.panel.MultiColorPanelWidget;
import org.joml.Matrix3x2fStack;

public class MultiColorPickerWidget extends HorizontalListWidget {

	private final RGBColorPickerWidget colorPicker;
	private final MultiColorPanelWidget panel;

	private final ISetting<MultiColor> setting;

	public MultiColorPickerWidget(ISetting<MultiColor> setting) {
		this.setting = setting;

		colorPicker = addChild(new RGBColorPickerWidget(setting.get().getFirst()));
		panel = addChild(new MultiColorPanelWidget(setting, this::select, this::reset));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		colorPicker.setWidth(AbstractColorPickerWidget.WIDTH);
		panel.setHeight(colorPicker.getHeight());

		setIndent(2.5f);

		super.onSizeUpdate(matrices);
	}

	public void reset() {
		panel.reset();
		select(setting.get().getFirst());
	}

	public void select(MutableColor mutableColor) {
		colorPicker.setMutableColor(mutableColor);
	}
}
