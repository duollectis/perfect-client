package com.perfect.client.core.functional.setting.interfaces.settings;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.text.filter.ITextFilter;

public interface IStringSetting extends ISetting<String> {

	ITextFilter getFilter();
}
