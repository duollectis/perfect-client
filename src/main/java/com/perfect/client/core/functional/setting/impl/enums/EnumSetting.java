package com.perfect.client.core.functional.setting.impl.enums;

import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.enums.IEnumSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.enums.EnumUtils;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.enums.impl.EnumWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumSetting<T extends Enum<T>> extends Setting<T> implements IEnumSetting<T, T> {

	private final T[] values;

	private EnumSetting(
			String name,
			T[] values,
			T defaultValue,
			Consumer<T> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.values = values;
	}

	public boolean is(T value) {
		return get() == value;
	}

	@Override
	public boolean isValueValid(T value) {
		return true;
	}

	@Override
	public void save(JsonObject json) {
		json.add("value", get().name());
	}

	@Override
	public void load(JsonObject json) {
		set(EnumUtils.valueFromName(values, json.getString("value")).orElse(defaultValue));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new EnumWidget<>(this);
	}

	@Override
	public T[] getValues() {
		return values;
	}

	@Override
	public boolean get(T value) {
		return value == get();
	}

	public static class Builder<T extends Enum<T>> extends SettingBuilder<Builder<T>, T, EnumSetting<T>> {

		private final T[] values;

		@SafeVarargs
		public Builder(T... values) {
			super(values[0]);
			this.values = values;
		}

		public Builder<T> onChangedMode(Supplier<ModeContainer<T, ?>> modeContainerSupplier) {
			this.onChanged = value -> modeContainerSupplier.get().set(value);
			return this;
		}

		@Override
		public EnumSetting<T> build() {
			return new EnumSetting<>(id, values, defaultValue, onChanged, visible, toggleable);
		}
	}
}
