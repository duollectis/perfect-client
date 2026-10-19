package com.perfect.client.gui.widgets.windows;

import com.perfect.client.core.functional.setting.interfaces.settings.IBundleSetting;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.settings.SettingsListWidget;

import java.util.function.Supplier;

public class BundleEditWindowWidget extends WindowWidget {

	private static final float INDENT = 1.8f;
	private static final float ROUNDING = 4.7f;

	public BundleEditWindowWidget(IBundleSetting<?> bundleSetting) {
		super(new SettingsListWidget(bundleSetting.getSettings()));

		setup(bundleSetting::getName);
	}

	private void setup(Supplier<String> nameSupplier) {
		setName(nameSupplier);
		setDisablePanel(false);

		setIndent(INDENT);
		setRounding(ROUNDING);
	}
}
