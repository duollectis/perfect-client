package com.perfect.client.core.functional.setting.impl.enums;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.enums.EnumOrder;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.order.OrderWidget;
import lombok.Getter;

import java.util.function.Consumer;

@Getter
public class EnumOrderSetting<T extends Enum<T>> extends Setting<EnumOrder<T>> implements ISetting<EnumOrder<T>> {

	private final T[] values;

	private EnumOrderSetting(
			String name,
			T[] values,
			EnumOrder<T> defaultValue,
			Consumer<EnumOrder<T>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.values = values;
	}

	public void move(T val, int index) {
		value.move(val, index);
		onChanged();
	}

	public int getOrder(T val) {
		return value.getOrder(val);
	}

	public int getPriority(T val) {
		return value.getPriority(val);
	}

	public void toggle(T val) {
		value.toggle(val);
		onChanged();
	}

	public boolean isEnabled(T val) {
		return value.isEnabled(val);
	}

	@Override
	public void resetImpl() {
		value = new EnumOrder<>(defaultValue);
	}

	@Override
	public boolean isValueValid(EnumOrder<T> value) {
		return true;
	}

	@Override
	protected void save(JsonObject json) {
		json.add("value", value.toJson());
	}

	@Override
	protected void load(JsonObject json) {
		value.fromJson(json.getJsonObject("value"));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new OrderWidget<>(this);
	}

	public static class Builder<T extends Enum<T>> extends SettingBuilder<Builder<T>, EnumOrder<T>, EnumOrderSetting<T>> {

		private final T[] values;

		@SafeVarargs
		public Builder(T... values) {
			super(new EnumOrder<>(values));
			this.values = values;
		}

		@Override
		public EnumOrderSetting<T> build() {
			return new EnumOrderSetting<>(id, values, defaultValue, onChanged, visible, toggleable);
		}
	}
}
