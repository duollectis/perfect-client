package com.perfect.client.core.functional.setting.impl.enums;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumListSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.enums.EnumUtils;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.enums.impl.EnumListWidget;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EnumListSetting<T extends Enum<?>> extends Setting<Set<T>> implements IEnumListSetting<Set<T>, T> {

	private final T[] values;

	public EnumListSetting(
			String name,
			T[] values,
			Set<T> defaultValue,
			Consumer<Set<T>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.values = values;
	}

	@Override
	public void resetImpl() {
		value = new LinkedHashSet<>(defaultValue);
	}

	@Override
	public boolean isValueValid(Set<T> value) {
		return true;
	}

	@Override
	public void save(JsonObject json) {
		JsonArray valuesJson = new JsonArray();

		for (T value : value) {
			if (value != null) {
				valuesJson.add(value.name());
			}
		}

		json.add("value", valuesJson);
	}

	@Override
	public void load(JsonObject json) {
		JsonArray valuesJson = json.getJsonArray("value");

		for (JsonElement<?> element : valuesJson) {
			EnumUtils.valueFromName(values, element.asString()).ifPresent(val -> value.add(val));
		}
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new EnumListWidget<>(this);
	}

	@Override
	public T[] getValues() {
		return values;
	}

	@Override
	public boolean get(T val) {
		return value.contains(val);
	}

	@Override
	public void add(T val) {
		value.add(val);
	}

	@Override
	public void remove(T val) {
		value.remove(val);
	}

	public static class Builder<T extends Enum<?>> extends SettingBuilder<Builder<T>, Set<T>, EnumListSetting<T>> {

		private final T[] values;

		@SafeVarargs
		public Builder(T... values) {
			super(new LinkedHashSet<>());
			this.values = values;
		}

		@SafeVarargs
		public final Builder<T> defaultValue(T... value) {
			defaultValue = new LinkedHashSet<>(Arrays.asList(value));
			return this;
		}

		@Override
		public EnumListSetting<T> build() {
			return new EnumListSetting<>(id, values, defaultValue, onChanged, visible, toggleable);
		}
	}
}
