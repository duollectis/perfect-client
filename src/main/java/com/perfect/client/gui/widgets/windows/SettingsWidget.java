package com.perfect.client.gui.widgets.windows;

import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.settings.SettingsListWidget;
import org.joml.Matrix3x2fStack;

public class SettingsWidget extends VerticalListWidget {

	private static final float VERTICAL_INDENT = 1.7f;

	public SettingsWidget(Settings settings) {
		settings.forEach(group -> addChild(new SettingsListWidget(group)));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setIndent(VERTICAL_INDENT);
		super.onSizeUpdate(matrices);
	}
}
