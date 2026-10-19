package com.perfect.client.core.functional.setting.impl.color;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.color.MultiColorWidget;

import java.util.function.Consumer;

public class MultiColorSetting extends Setting<MultiColor> {

	public MultiColorSetting(
			String name,
			MultiColor defaultValue,
			Consumer<MultiColor> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
	}

	@Override
	public boolean isValueValid(MultiColor value) {
		return true;
	}

	@Override
	public void resetImpl() {
		value = new MultiColor(defaultValue);
	}

	@Override
	protected void save(JsonObject json) {
		json.add("value", value.toJson());
	}

	@Override
	public void load(JsonObject json) {
		value.fromJson(json.getJsonObject("value"));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new MultiColorWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, MultiColor, MultiColorSetting> {

		public Builder() {
			super(new MultiColor());
		}

		@Override
		public MultiColorSetting build() {
			return new MultiColorSetting(id, defaultValue, onChanged, visible, toggleable);
		}
	}
}
