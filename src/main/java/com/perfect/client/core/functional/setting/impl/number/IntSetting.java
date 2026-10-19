package com.perfect.client.core.functional.setting.impl.number;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.number.INumberSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.slider.number.IntSliderWidget;

import java.util.function.Consumer;

public class IntSetting extends Setting<Integer> implements INumberSetting<Integer> {

	private final int min, max;
	private final int sliderMin, sliderMax;
	private final String suffix;

	private boolean widgetDragging;

	private IntSetting(
			String name,
			int defaultValue,
			Consumer<Integer> onChanged,
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
	public void setWidgetDragging(boolean dragging) {
		widgetDragging = dragging;
	}

	@Override
	public boolean isWidgetDragging() {
		return widgetDragging;
	}

	@Override
	public boolean isValueValid(Integer value) {
		return value >= min && value <= max;
	}

	@Override
	public void save(JsonObject json) {
		json.add("value", get());
	}

	@Override
	public void load(JsonObject json) {
		set(json.getInt("value", defaultValue));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new IntSliderWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Integer, IntSetting> {

		private int min = 0, max = 100000;
		private int sliderMin = 0, sliderMax = 10;
		public String suffix = "";

		public Builder() {
			super(0);
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
			this.sliderMin = min;
			return this;
		}

		public Builder sliderMax(int max) {
			this.sliderMax = max;
			return this;
		}

		public Builder sliderRange(int min, int max) {
			this.sliderMin = min;
			this.sliderMax = max;
			return this;
		}

		public Builder suffix(String suffix) {
			this.suffix = suffix;
			return this;
		}

		@Override
		public IntSetting build() {
			return new IntSetting(
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
