package com.perfect.client.core.functional.setting;

import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class Settings implements Iterable<SettingGroup> {

	private final List<BaseSetting> settings = new ArrayList<>();
	private final List<SettingGroup> groups = new ArrayList<>();

	public boolean isEmpty() {
		return groups.isEmpty();
	}

	public boolean isInvisible() {
		if (isEmpty()) {
			return true;
		}

		for (SettingGroup sg : this) {
			for (BaseSetting setting : sg) {
				if (setting.isVisible()) {
					return false;
				}
			}
		}

		return true;
	}

	public void forEachSetting(Consumer<BaseSetting> action) {
		forEach(group -> group.forEach(action));
	}

	public SettingGroup createGroup() {
		return createGroup(groups.size());
	}

	public SettingGroup createGroup(int index) {
		SettingGroup group = new SettingGroup(settings);
		groups.add(index, group);
		return group;
	}

	public void reset() {
		settings.forEach(baseSetting -> {
			if (baseSetting instanceof Setting<?> setting) {
				setting.reset();
			}
		});
	}

	@Override
	public @NotNull Iterator<SettingGroup> iterator() {
		return groups.iterator();
	}

	public JsonArray toJson() {
		JsonArray json = new JsonArray();

		for (BaseSetting baseSetting : settings) {
			if (baseSetting instanceof Setting<?> setting && setting.wasChanged()) {
				json.add(setting.toJson());
			}
		}

		return json;
	}

	public void fromJson(JsonArray json) {
		for (JsonElement<?> element : json) {
			JsonObject settingJson = element.asJsonObject();

			Setting<?> setting = get(settingJson.getString("name"));

			if (setting != null) {
				setting.fromJson(settingJson);
			}
		}
	}

	public Setting<?> get(String id) {
		for (BaseSetting baseSetting : settings) {
			if (baseSetting instanceof Setting<?> setting && setting.getId().equals(id)) {
				return setting;
			}
		}

		return null;
	}
}
