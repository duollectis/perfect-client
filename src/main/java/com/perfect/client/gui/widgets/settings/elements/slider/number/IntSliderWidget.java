package com.perfect.client.gui.widgets.settings.elements.slider.number;

import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFilters;

public class IntSliderWidget extends NumberSliderWidget<Integer> {

	public IntSliderWidget(IntSetting setting) {
		super(setting);
	}

	@Override
	protected void setSliderValue(double value) {
		setting.set(Math.clamp(Math.round(value), setting.getSliderMin(), setting.getSliderMax()));
	}

	@Override
	protected void setValue(String value) {
		if (value.isEmpty()) {
			setting.set(setting.getDefaultValue());
		}
		else {
			setting.set(Math.clamp(StringUtils.parseInt(value).orElse(0), setting.getMin(), setting.getMax()));
		}
	}

	@Override
	protected ITextFilter getStringFilter() {
		return TextFilters.INT;
	}

	@Override
	protected String getValueString(double value) {
		return (int) Math.round(value) + "";
	}
}
