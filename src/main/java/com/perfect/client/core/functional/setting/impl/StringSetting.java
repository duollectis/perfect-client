package com.perfect.client.core.functional.setting.impl;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.interfaces.settings.IStringSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.text.filter.ITextFilter;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.StringWidget;

import java.util.function.Consumer;

public class StringSetting extends Setting<String> implements IStringSetting {

	private final ITextFilter filter;

	private StringSetting(
			String name,
			String defaultValue,
			Consumer<String> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			ITextFilter filter
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.filter = filter;
	}

	@Override
	public ITextFilter getFilter() {
		return filter;
	}

	@Override
	public boolean isValueValid(String value) {
		return filter == null || filter.test(value);
	}

	@Override
	public void save(JsonObject json) {
		json.add("value", value);
	}

	@Override
	public void load(JsonObject json) {
		set(json.getString("value", defaultValue));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new StringWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, String, StringSetting> {

		private ITextFilter textFilter;

		public Builder() {
			super("");
		}

		public Builder filter(ITextFilter textFilter) {
			this.textFilter = textFilter;
			return this;
		}

		@Override
		public StringSetting build() {
			return new StringSetting(id, defaultValue, onChanged, visible, toggleable, textFilter);
		}
	}
}
