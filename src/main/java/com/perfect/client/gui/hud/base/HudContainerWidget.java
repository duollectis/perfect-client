package com.perfect.client.gui.hud.base;

import com.perfect.client.core.utils.files.IIdentifiable;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.hud.elements.*;
import com.perfect.client.gui.hud.elements.functions.FunctionsHud;
import com.perfect.client.gui.hud.elements.potions.PotionsHud;
import com.perfect.client.gui.hud.elements.speed.SpeedHud;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

@Setter
public class HudContainerWidget extends Widget {

	private float scale = 1.0f;

	public HudContainerWidget() {
		addChild(new ArmorHud());
		addChild(new BindsHud());
		addChild(new CoordinatesHud());
		addChild(new FpsHud());
		addChild(new FunctionsHud());
		addChild(new InventoryHud());
		addChild(new PingHud());
		addChild(new PotionsHud());
		addChild(new SpeedHud());
		addChild(new TpsHud());
		addChild(new WatermarkHud());
		addChild(new WelcomerHud());

		initWidgets();
	}

	@Override
	public void onPreTick() {
		getChildren().forEach(widget -> widget.multiplyScale(scale));
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(DrawConstants.getScreenWidth());
		setHeight(DrawConstants.getScreenHeight());
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();

		for (Widget widget : getChildren()) {
			if (widget instanceof IIdentifiable<?> serializable) {
				JsonObject widgetJson = serializable.toJson();
				if (widgetJson != null) {
					jsonArray.add(widgetJson);
				}
			}
		}

		return jsonArray;
	}

	public void fromJson(JsonArray json) {
		for (JsonElement<?> element : json) {
			JsonObject settingJson = element.asJsonObject();

			HudWidget setting = get(settingJson.getString("name"));

			if (setting != null) {
				setting.fromJson(settingJson);
			}
		}
	}

	public HudWidget get(String id) {
		for (Widget child : getChildren()) {
			if (child instanceof HudWidget widget && widget.getId().equals(id)) {
				return widget;
			}
		}

		return null;
	}

	public void initWidgets() {
		getChildren().stream()
		             .map(HudWidget.class::cast)
		             .forEach(hudWidget -> hudWidget
				             .getSettings()
				             .forEach(group -> group
						             .forEach(baseSetting -> baseSetting
								             .setHudWidget(hudWidget))
				             )
		             );
	}

	public void reset() {
		getChildren().stream()
		             .map(HudWidget.class::cast)
		             .forEach(hudWidget -> {
			             hudWidget.setNormalizedX(hudWidget.getDefaultX());
			             hudWidget.setNormalizedY(hudWidget.getDefaultY());
		             });
	}
}
