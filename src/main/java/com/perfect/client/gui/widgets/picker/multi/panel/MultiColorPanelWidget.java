package com.perfect.client.gui.widgets.picker.multi.panel;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.color.multiColor.MutableColor;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.picker.multi.panel.settings.MultiColorSettingsWidget;
import org.joml.Matrix3x2fStack;

import java.util.function.Consumer;

public class MultiColorPanelWidget extends VerticalListWidget {

	private final MultiColorSettingsWidget settings;
	private final ColorButtonsWidget buttons;

	public MultiColorPanelWidget(
			ISetting<MultiColor> setting,
			Consumer<MutableColor> selectColorConsumer,
			Runnable resetRunnable
	) {
		settings = addChild(new MultiColorSettingsWidget(setting, selectColorConsumer));
		buttons = addChild(new ColorButtonsWidget(setting, resetRunnable));

		expandWidth();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(2.25f);
		setWidth(53.25f);

		settings.setHeight(height - buttons.getHeight() - indent);

		super.onSizeUpdate(matrices);
	}

	public void reset() {
		settings.reset();
	}
}
