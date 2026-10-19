package com.perfect.client.gui.widgets.settings.elements.slider.range;

import com.perfect.client.core.functional.setting.interfaces.settings.number.IRangeSetting;
import com.perfect.client.core.utils.range.IntRange;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.core.utils.text.filter.TextFilters;

public class IntRangeWidget extends RangeWidget<Integer, IntRange, IRangeSetting<Integer, IntRange>> {

	public IntRangeWidget(IRangeSetting<Integer, IntRange> setting) {
		super(setting);
	}

	@Override
	protected void setFrom(String value) {
		int parsed = parse(value, setting.getDefaultValue().getFrom());

		if (parsed > setting.get().getTo()) {
			setting.setTo(parsed);
		}
		else {
			setting.setFrom(parsed);
		}
	}

	@Override
	protected void setTo(String value) {
		int parsed = parse(value, setting.getDefaultValue().getTo());

		if (parsed < setting.get().getFrom()) {
			setting.setFrom(parsed);
		}
		else {
			setting.setTo(parsed);
		}
	}

	private int parse(String value, int defaultValue) {
		if (value.isEmpty()) {
			return defaultValue;
		}
		else {
			int parsed = StringUtils.parseInt(value).orElse(0);
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

	private int normalizeValue(double val) {
		return Math.clamp((int) Math.round(val), setting.getSliderMin(), setting.getSliderMax());
	}

	@Override
	protected String getValueString(double value) {
		return (int) value + "";
	}
}
