package com.perfect.client.manager.system;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.utils.WindowSerialize;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UIManager extends Manager implements ISystem<UIManager> {

	private final List<WindowSerialize> windows = new ArrayList<>();

	private final WindowSerialize themeWindow = addWindow("theme-window");
	private final WindowSerialize clientSettingsWindow = addWindow("settings-window");
	private final WindowSerialize hudSettingsWindow = addWindow("hud-settings-window");
	private final WindowSerialize configsWindow = addWindow("configs-window");

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		windows.forEach(window -> json.add(window.getId(), window.toJson()));

		return json;
	}

	@Override
	public UIManager fromJson(JsonObject json) {
		windows.forEach(window -> window.fromJson(json.getJsonObject(window.getId())));
		return this;
	}

	@Override
	public String getId() {
		return "ui";
	}

	private WindowSerialize addWindow(String id) {
		WindowSerialize window = new WindowSerialize(id);
		windows.add(window);
		return window;
	}

	@Override
	public void reset() {
		windows.forEach(WindowSerialize::reset);
	}
}
