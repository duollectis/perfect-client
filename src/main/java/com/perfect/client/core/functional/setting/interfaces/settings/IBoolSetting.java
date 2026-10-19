package com.perfect.client.core.functional.setting.interfaces.settings;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.setting.interfaces.ISetting;

public interface IBoolSetting extends ISetting<Boolean> {

	AbstractBindListener getBindListener();
}
