package com.perfect.client.gui.widgets.settings;

import com.perfect.client.core.functional.setting.BaseSetting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import com.perfect.client.gui.widgets.function.FunctionWidget;
import org.joml.Matrix3x2fStack;

import java.util.List;

public class SettingsListWidget extends Widget {

	public static final float DEPTH = 0f;
	public static final float WIDTH = FunctionWidget.WIDTH;

	private final VerticalListWidget list = addChild(new VerticalListWidget());

	public SettingsListWidget(Settings settings) {
		settings.forEach(group -> list.addChild(new SettingsGroupWidget(group)));
	}

	public SettingsListWidget(SettingGroup group) {
		list.addChild(new SettingsGroupWidget(group));
	}

	@Override
	public void onAnimationTick() {
		List<Widget> widgets = list.getChildren();

		widgets.forEach(widget -> {
			if (widget instanceof SettingsGroupWidget settingsGroupWidget) {
				boolean visible = isGroupVisible(settingsGroupWidget.getGroup());
				settingsGroupWidget.setVisible(visible);
				settingsGroupWidget.setActive(visible);
				settingsGroupWidget.setSeparate(false);
			}
		});

		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);

			if (widget.shouldDraw() && widget instanceof SettingsGroupWidget settingsGroupWidget) {
				for (int n = i + 1; n < widgets.size(); n++) {
					Widget nextWidget = widgets.get(n);

					if (nextWidget.shouldDraw()) {
						settingsGroupWidget.setSeparate(true);
						break;
					}
				}
			}
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		width = WIDTH;
		height = list.getHeight() + SettingWidget.PLATE_DEPTH * 2f;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		list.setX(SettingWidget.PLATE_DEPTH);
		list.setY(SettingWidget.PLATE_DEPTH);
	}

	private static boolean isGroupVisible(SettingGroup group) {
		for (BaseSetting baseSetting : group) {
			if (baseSetting.isVisible()) {
				return true;
			}
		}

		return false;
	}
}
