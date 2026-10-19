package com.perfect.client.manager.system;

import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.WidgetContainer;
import com.perfect.client.gui.hud.HudEditorScreen;
import com.perfect.client.gui.hud.HudSettings;
import com.perfect.client.gui.hud.base.HudContainerWidget;
import lombok.Getter;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.hudSettings;

public class HudManager extends Manager implements ISystem<HudManager> {

	private final HudSettings hud;

	@Getter
	private final WidgetContainer widgetContainer = new WidgetContainer().customScale().hudLayer();

	@Getter
	private final HudContainerWidget hudWidget = widgetContainer.addChild(new HudContainerWidget());

	private float scale = 1.0f;

	public HudManager() {
		hud = new HudSettings();

		hudSettings = hud;
	}

	@Override
	public void onGuiRender(Render2DEvent event) {
		if (isHudVisible()) {
			hudWidget.setScale(getScale());
			widgetContainer.open();
		}
		else {
			widgetContainer.close();
		}
	}

	@Override
	public String getId() {
		return "hud";
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("settings", hud.getSettings().toJson());
		json.add("elements", hudWidget.toJson());

		return json;
	}

	@Override
	public HudManager fromJson(JsonObject json) {
		hud.getSettings().fromJson(json.getJsonArray("settings"));
		hudWidget.fromJson(json.getJsonArray("elements"));

		return this;
	}

	@Override
	public void reset() {
		hud.getSettings().reset();
		hudWidget.reset();
	}

	private boolean isHudVisible() {
		if (mc.options.hudHidden) {
			return false;
		}

		return mc.currentScreen instanceof HudEditorScreen || (mc.world != null && hud.getEnabled().get());
	}

	public float getScale() {
		if (!hud.getScale().isWidgetDragging()) {
			scale = hud.getScale().getFloat();
		}

		return scale * clientConfig.getGuiScale();
	}
}
