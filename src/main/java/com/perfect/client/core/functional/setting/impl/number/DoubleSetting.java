package com.perfect.client.core.functional.setting.impl.number;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.number.INumberSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.number.DoubleSliderWidget;

import java.util.function.Consumer;

public class DoubleSetting extends Setting<Double> implements INumberSetting<Double> {

	private final double min, max;
	private final double sliderMin, sliderMax;
	private final String suffix;

	private boolean widgetDragging;

	private DoubleSetting(
			String name,
			double defaultValue,
			Consumer<Double> onChanged,
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
	public void setWidgetDragging(boolean dragging) {
		widgetDragging = dragging;
	}

	@Override
	public boolean isWidgetDragging() {
		return widgetDragging;
	}

	@Override
	public boolean isValueValid(Double value) {
		return value >= min && value <= max;
	}

	@Override
	protected void save(JsonObject json) {
		json.add("value", get());
	}

	@Override
	public void load(JsonObject json) {
		set(json.getDouble("value", defaultValue));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new DoubleSliderWidget(this);
	}

	public int getInt() {
		return get().intValue();
	}

	public float getFloat() {
		return get().floatValue();
	}

	public double fromPercent() {
		return get() / 100.0;
	}

	public double fromBPS() {
		return get() / 20.0;
	}

	public int fromSeconds() {
		return (int) (get() * 1000.0);
	}

	public static class Builder extends SettingBuilder<Builder, Double, DoubleSetting> {

		private double min = 0.0, max = 100000.0;
		private double sliderMin = 0.0, sliderMax = 10.0;
		private String suffix = "";

		public Builder() {
			super(0.0);
		}

		public Builder defaultValue(double defaultValue) {
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
		public DoubleSetting build() {
			return new DoubleSetting(
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
