package com.perfect.client.core.functional.setting.interfaces;

import com.perfect.client.core.utils.IVisible;

public interface IBaseSetting extends IVisible {

	String getName();
	String getDescription();
	String getFunctionId();
	String getHudWidgetId();
}
