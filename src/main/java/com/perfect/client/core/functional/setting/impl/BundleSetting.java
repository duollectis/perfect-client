package com.perfect.client.core.functional.setting.impl;

import com.perfect.client.core.functional.function.config.SettingBundle;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.functional.setting.interfaces.settings.IBundleSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.BundleWidget;

import java.util.function.Consumer;

public class BundleSetting<T extends SettingBundle> extends Setting<T> implements IBundleSetting<T> {

	public BundleSetting(
			String name,
			T defaultValue,
			Consumer<T> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
	}

	@Override
	public void reset() {
		value.reset();
	}

	@Override
	public boolean wasChanged() {
		return value.wasChanged();
	}

	@Override
	public boolean isValueValid(T value) {
		return false;
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
		return new BundleWidget<>(this);
	}

	@Override
	public Settings getSettings() {
		return value.settings;
	}

	public static class Builder<T extends SettingBundle> extends SettingBuilder<BundleSetting.Builder<T>, T, BundleSetting<T>> {

		public Builder() {
			super(null);
		}

		@Override
		public BundleSetting<T> build() {
			return new BundleSetting<>(id, defaultValue, onChanged, visible, toggleable);
		}
	}
}
