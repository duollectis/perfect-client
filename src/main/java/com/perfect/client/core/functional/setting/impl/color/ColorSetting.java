package com.perfect.client.core.functional.setting.impl.color;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IColorSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.color.ColorWidget;

import java.awt.*;
import java.util.function.Consumer;

public class ColorSetting extends Setting<Color> implements IColorSetting {

	public ColorSetting(
			String name,
			Color defaultValue,
			Consumer<Color> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
	}

	@Override
	public Color getColor() {
		return get();
	}

	@Override
	public void setColor(Color color) {
		set(color);
	}

	@Override
	public boolean isValueValid(Color value) {
		return true;
	}

	@Override
	public void resetImpl() {
		value = ColorUtils.copy(defaultValue);
	}

	@Override
	protected void save(JsonObject json) {
		json.add("value", ColorUtils.toJson(getColor()));
	}

	@Override
	public void load(JsonObject json) {
		setColor(ColorUtils.fromJson(json.getJsonObject("value")));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new ColorWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Color, ColorSetting> {

		public Builder() {
			super(Color.WHITE);
		}

		@Override
		public ColorSetting build() {
			return new ColorSetting(id, defaultValue, onChanged, visible, toggleable);
		}
	}
}
