package com.perfect.client.core.functional.setting.interfaces.settings;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.setting.interfaces.ISetting;

public interface IBindSetting extends ISetting<Bind> {

	AbstractBindListener getListener();
}
