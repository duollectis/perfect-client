package com.perfect.client.core.functional.setting.interfaces.settings;

import com.perfect.client.core.functional.function.config.SettingBundle;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.functional.setting.interfaces.ISetting;

public interface IBundleSetting<T extends SettingBundle> extends ISetting<T> {

	Settings getSettings();
}
