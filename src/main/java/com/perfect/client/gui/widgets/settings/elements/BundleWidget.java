package com.perfect.client.gui.widgets.settings.elements;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.functional.function.config.SettingBundle;
import com.perfect.client.core.functional.setting.interfaces.settings.IBundleSetting;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.windows.BundleEditWindowWidget;
import org.joml.Matrix3x2fStack;

public class BundleWidget<T extends SettingBundle> extends SettingWidget<T, IBundleSetting<T>> {

	public BundleWidget(IBundleSetting<T> setting) {
		super(setting);
	}

	@Override
	public float getBorderedWidth() {
		return 4.4f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setHeight(TEXT_HEIGHT);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		// ой похуй потом
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			openWindow();
		}
	}

	private void openWindow() {
		container.get().addWindow(new BundleEditWindowWidget(setting));
	}
}
