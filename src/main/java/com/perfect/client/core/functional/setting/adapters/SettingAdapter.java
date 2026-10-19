package com.perfect.client.core.functional.setting.adapters;

import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import lombok.Setter;

import java.util.function.Function;
import java.util.function.Supplier;

@Setter
public abstract class SettingAdapter<T> implements ISetting<T> {

	private Supplier<String> nameSupplier;
	private Supplier<T> valueSupplier;
	private Function<T, Boolean> valueFunction;
	private Supplier<T> defaultValueSupplier;
	private Supplier<Boolean> visibleSupplier;
	private Runnable resetAction;

	@Override
	public String getName() {
		if (nameSupplier == null) {
			return null;
		}

		return nameSupplier.get();
	}

	@Override
	public T get() {
		if (valueSupplier == null) {
			return null;
		}

		return valueSupplier.get();
	}

	@Override
	public T getDefaultValue() {
		if (defaultValueSupplier == null) {
			return null;
		}

		return defaultValueSupplier.get();
	}

	@Override
	public boolean set(T value) {
		if (valueFunction == null) {
			return true;
		}

		return valueFunction.apply(value);
	}

	@Override
	public void reset() {
		if (resetAction != null) {
			resetAction.run();
		}
	}

	@Override
	public boolean isVisible() {
		if (visibleSupplier == null) {
			return true;
		}

		return visibleSupplier.get();
	}

	@Override
	public IBoolSetting getToggleable() {
		return null;
	}
}
