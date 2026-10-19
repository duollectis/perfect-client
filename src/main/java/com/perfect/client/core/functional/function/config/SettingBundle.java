package com.perfect.client.core.functional.function.config;

import com.perfect.client.core.functional.setting.BaseSetting;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;

public class SettingBundle implements ISerializable<SettingBundle> {

	public final Settings settings = new Settings();

	public void reset() {
		settings.reset();
	}

	public boolean wasChanged() {
		for (SettingGroup groups : settings) {
			for (BaseSetting baseSetting : groups) {
				if (baseSetting instanceof Setting<?> setting && setting.wasChanged()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("settings", settings.toJson());
		return json;
	}

	@Override
	public SettingBundle fromJson(JsonObject json) {
		settings.fromJson(json.getJsonArray("settings"));
		return this;
	}
}
