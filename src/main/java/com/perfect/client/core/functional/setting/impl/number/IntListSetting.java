package com.perfect.client.core.functional.setting.impl.number;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.MutableInt;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.IntListWidget;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class IntListSetting extends Setting<List<MutableInt>> implements ISetting<List<MutableInt>> {

	private final int min;
	private final int max;

	@Getter
	private final int maxLength;

	private IntListSetting(
			String id,
			List<MutableInt> defaultValue,
			Consumer<List<MutableInt>> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			int min, int max
	) {
		super(id, defaultValue, onChanged, visible, toggleable);
		this.min = min;
		this.max = max;

		maxLength = Math.max(
				Integer.toString(min).length(),
				Integer.toString(max).length()
		);
	}

	public void set(MutableInt integer, int val) {
		integer.setValue(Math.clamp(val, min, max));
		onChanged();
	}

	public void remove(MutableInt integer) {
		value.remove(integer);
		onChanged();
	}

	public void add(MutableInt integer) {
		value.add(integer);
		onChanged();
	}

	@Override
	public void resetImpl() {
		value = new ArrayList<>(defaultValue);
	}

	@Override
	public boolean isValueValid(List<MutableInt> value) {
		return value.stream().noneMatch(integer -> integer.getValue() < min || integer.getValue() > max);
	}

	@Override
	protected void save(JsonObject json) {
		JsonArray jsonArray = new JsonArray();

		for (MutableInt integer : value) {
			jsonArray.add(integer.getValue());
		}

		json.add("value", jsonArray);
	}

	@Override
	protected void load(JsonObject json) {
		List<MutableInt> value = new ArrayList<>();
		JsonArray jsonArray = json.getJsonArray("value");

		for (JsonElement<?> element : jsonArray) {
			int integer = element.asInt();
			value.add(new MutableInt(integer));
		}

		set(value);
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new IntListWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, List<MutableInt>, IntListSetting> {

		private int min = 0;
		private int max = 100;

		public Builder() {
			super(new ArrayList<>());
		}

		public Builder min(int min) {
			this.min = min;
			return this;
		}

		public Builder max(int max) {
			this.max = max;
			return this;
		}

		public Builder defaultValue(int... defaults) {
			for (int def : defaults) {
				defaultValue.add(new MutableInt(def));
			}
			return this;
		}

		@Override
		public IntListSetting build() {
			return new IntListSetting(id, defaultValue, onChanged, visible, toggleable, min, max);
		}
	}
}
