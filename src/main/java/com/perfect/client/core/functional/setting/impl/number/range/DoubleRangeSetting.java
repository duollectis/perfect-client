package com.perfect.client.core.functional.setting.impl.number.range;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.number.IRangeSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.range.DoubleRange;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.range.DoubleRangeWidget;

import java.util.function.Consumer;

public class DoubleRangeSetting extends Setting<DoubleRange> implements IRangeSetting<Double, DoubleRange> {

	private final double min, max;
	private final double sliderMin, sliderMax;
	private final String suffix;

	private DoubleRangeSetting(
			String name,
			DoubleRange defaultValue,
			Consumer<DoubleRange> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			double min, double max,
			double sliderMin, double sliderMax,
			String suffix
	) {
		super(name, defaultValue, onChanged, visible, toggleable);

		this.min = min;
		this.max = max;
		this.sliderMin = sliderMin;
		this.sliderMax = sliderMax;
		this.suffix = suffix;
	}

	@Override
	public void resetImpl() {
		value = new DoubleRange(defaultValue);
	}

	@Override
	public Double getMin() {
		return min;
	}

	@Override
	public Double getMax() {
		return max;
	}

	@Override
	public Double getSliderMin() {
		return sliderMin;
	}

	@Override
	public Double getSliderMax() {
		return sliderMax;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public boolean isValueValid(DoubleRange value) {
		return value.getFrom() >= min && value.getTo() <= max;
	}

	@Override
	protected void save(JsonObject json) {
		json.add("from", getFrom());
		json.add("to", getTo());
	}

	@Override
	public void load(JsonObject json) {
		double from = json.getDouble("from", defaultValue.getFrom());
		double to = json.getDouble("to", defaultValue.getTo());

		set(new DoubleRange(from, to));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new DoubleRangeWidget(this);
	}

	@Override
	public Double getFrom() {
		return value.getFrom();
	}

	@Override
	public Double getTo() {
		return value.getTo();
	}

	@Override
	public void setFrom(Double val) {
		set(new DoubleRange(val, value.getTo()));
	}

	@Override
	public void setTo(Double val) {
		set(new DoubleRange(value.getFrom(), val));
	}

	public static class Builder extends SettingBuilder<Builder, DoubleRange, DoubleRangeSetting> {

		private double min = 0, max = 100000;
		private double sliderMin = 0, sliderMax = 10;
		private String suffix = "";

		public Builder() {
			super(new DoubleRange(0, 0));
		}

		public Builder defaultValue(double from, double to) {
			this.defaultValue = new DoubleRange(from, to);
			return this;
		}

		public Builder defaultValue(DoubleRange defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder min(double min) {
			this.min = min;
			return this;
		}

		public Builder max(double max) {
			this.max = max;
			return this;
		}

		public Builder range(double min, double max) {
			this.min = Math.min(min, max);
			this.max = Math.max(min, max);
			return this;
		}

		public Builder sliderMin(double min) {
			sliderMin = min;
			return this;
		}

		public Builder sliderMax(double max) {
			sliderMax = max;
			return this;
		}

		public Builder sliderRange(double min, double max) {
			sliderMin = min;
			sliderMax = max;
			return this;
		}

		public Builder suffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		@Override
		public DoubleRangeSetting build() {
			return new DoubleRangeSetting(
					id,
					defaultValue,
					onChanged,
					visible,
					toggleable,
					min,
					max,
					Math.max(sliderMin, min),
					Math.min(sliderMax, max),
					suffix
			);
		}
	}
}
