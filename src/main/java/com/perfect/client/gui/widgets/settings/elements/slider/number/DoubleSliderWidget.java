package com.perfect.client.gui.widgets.settings.elements.slider.number;

import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFilters;

public class DoubleSliderWidget extends NumberSliderWidget<Double> {

	public DoubleSliderWidget(DoubleSetting setting) {
		super(setting);
	}

	@Override
	protected void setSliderValue(double val) {
		setting.set(Math.clamp(Math.round(val * 10.0) / 10.0, setting.getSliderMin(), setting.getSliderMax()));
	}

	@Override
	protected void setValue(String value) {
		if (value.isEmpty()) {
			setting.set(setting.getDefaultValue());
		}
		else {
			double parsed = StringUtils.parseDoubleLimited(value).orElse(0.0);
			setting.set(Math.clamp(parsed, setting.getMin(), setting.getMax()));
		}
	}

	@Override
	protected ITextFilter getStringFilter() {
		return TextFilters.DOUBLE;
	}

	@Override
	protected String getValueString(double value) {
		return value + "";
	}

}
