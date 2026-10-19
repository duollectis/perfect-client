package com.perfect.client.gui.widgets.settings.elements.slider.range;

import com.perfect.client.core.functional.setting.interfaces.settings.number.IRangeSetting;
import com.perfect.client.core.utils.range.DoubleRange;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFilters;

public class DoubleRangeWidget extends RangeWidget<Double, DoubleRange, IRangeSetting<Double, DoubleRange>> {

	public DoubleRangeWidget(IRangeSetting<Double, DoubleRange> setting) {
		super(setting);
	}

	@Override
	protected void setFrom(String value) {
		double parsed = parse(value, setting.getDefaultValue().getFrom());

		if (parsed > setting.get().getTo()) {
			setting.setTo(parsed);
		}
		else {
			setting.setFrom(parsed);
		}
	}

	@Override
	protected void setTo(String value) {
		double parsed = parse(value, setting.getDefaultValue().getTo());

		if (parsed < setting.get().getFrom()) {
			setting.setFrom(parsed);
		}
		else {
			setting.setTo(parsed);
		}
	}

	private double parse(String value, double defaultValue) {
		if (value.isEmpty()) {
			return defaultValue;
		}
		else {
			double parsed = StringUtils.parseDoubleLimited(value).orElse(0.0);
			return Math.clamp(parsed, setting.getMin(), setting.getMax());
		}
	}

	@Override
	protected ITextFilter getStringFilter() {
		return TextFilters.DOUBLE;
	}

	@Override
	protected void setSliderFrom(double val) {
		setting.setFrom(normalizeValue(val));
	}

	@Override
	protected void setSliderTo(double val) {
		setting.setTo(normalizeValue(val));
	}

	private double normalizeValue(double val) {
		return Math.clamp(Math.round(val * 10.0) / 10.0, setting.getSliderMin(), setting.getSliderMax());
	}

	@Override
	protected String getValueString(double value) {
		return value + "";
	}
}
