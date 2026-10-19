package com.perfect.client.core.functional.setting.impl.number.range;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.number.IRangeSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.range.IntRange;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.range.IntRangeWidget;

import java.util.function.Consumer;

public class IntRangeSetting extends Setting<IntRange> implements IRangeSetting<Integer, IntRange> {

	private final int min, max;
	private final int sliderMin, sliderMax;
	private final String suffix;

	private IntRangeSetting(
			String name,
			IntRange defaultValue,
			Consumer<IntRange> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			int min, int max,
			int sliderMin, int sliderMax,
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
		value = new IntRange(defaultValue);
	}

	@Override
	public Integer getMin() {
		return min;
	}

	@Override
	public Integer getMax() {
		return max;
	}

	@Override
	public Integer getSliderMin() {
		return sliderMin;
	}

	@Override
	public Integer getSliderMax() {
		return sliderMax;
	}

	@Override
	public String getSuffix() {
		return suffix;
	}

	@Override
	public boolean isValueValid(IntRange value) {
		return value.getFrom() >= min && value.getTo() <= max;
	}

	@Override
	protected void save(JsonObject json) {
		json.add("from", getFrom());
		json.add("to", getTo());
	}

	@Override
	public void load(JsonObject json) {
		int from = json.getInt("from", defaultValue.getFrom());
		int to = json.getInt("to", defaultValue.getTo());

		set(new IntRange(from, to));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new IntRangeWidget(this);
	}

	@Override
	public Integer getFrom() {
		return value.getFrom();
	}

	@Override
	public Integer getTo() {
		return value.getTo();
	}

	@Override
	public void setFrom(Integer val) {
		set(new IntRange(val, value.getTo()));
	}

	@Override
	public void setTo(Integer val) {
		set(new IntRange(value.getFrom(), val));
	}

	public static class Builder extends SettingBuilder<Builder, IntRange, IntRangeSetting> {

		private int min = 0, max = 100000;
		private int sliderMin = 0, sliderMax = 10;
		private String suffix = "";

		public Builder() {
			super(new IntRange(0, 0));
		}

		public Builder defaultValue(int from, int to) {
			this.defaultValue = new IntRange(from, to);
			return this;
		}

		public Builder defaultValue(IntRange defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder min(int min) {
			this.min = min;
			return this;
		}

		public Builder max(int max) {
			this.max = max;
			return this;
		}

		public Builder range(int min, int max) {
			this.min = Math.min(min, max);
			this.max = Math.max(min, max);
			return this;
		}

		public Builder sliderMin(int min) {
			sliderMin = min;
			return this;
		}

		public Builder sliderMax(int max) {
			sliderMax = max;
			return this;
		}

		public Builder sliderRange(int min, int max) {
			sliderMin = min;
			sliderMax = max;
			return this;
		}

		public Builder suffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		@Override
		public IntRangeSetting build() {
			return new IntRangeSetting(
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
