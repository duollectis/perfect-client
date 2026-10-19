package com.perfect.client.manager.system;

import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.files.ISystem;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class ColorManager extends Manager implements ISystem<ColorManager> {

	public final ColorPreset MAIN = new ColorPreset(0.54f, 1f);

	public final ColorPreset BACKGROUND = new ColorPreset(0.72f, 0.1f);
	public final ColorPreset DEFAULT_OUTLINE = new ColorPreset(0.41f, 0.19f);
	public final ColorPreset SETTING_BACKGROUND = new ColorPreset(0.66f, 0.13f);

	public final ColorPreset FUNCTION_BACKGROUND = new ColorPreset(0.52f, 0.15f);
	public final ColorPreset FUNCTION_HOVERED = new ColorPreset(0.46f, 0.41f);

	public final ColorPreset SETTING = new ColorPreset(0.56f, 0.16f);
	public final ColorPreset ENUM_ACTIVE = new ColorPreset(0.62f, 0.35f);
	public final ColorPreset BIND = new ColorPreset(0.56f, 0.45f);

	public final ColorPreset HUD_BACKGROUND = new ColorPreset(0.72f, 0.1f);
	public final ColorPreset HUD_OUTLINE = new ColorPreset(0.41f, 0.19f);

	public final ColorPreset LIST_SETTING_BACKGROUND = new ColorPreset(0.56f, 0.17f);
	public final ColorPreset LIST_SETTING_OUTLINE = new ColorPreset(0.36f, 0.28f);
	public final ColorPreset LIST_SETTING_ENABLED = new ColorPreset(0.53f, 0.44f);
	public final ColorPreset LIST_DESCRIPTION_BACKGROUND = new ColorPreset(0.48f, 0.24f);
	public final ColorPreset LIST_DESCRIPTION_OUTLINE = new ColorPreset(0.39f, 0.36f);

	public final ColorPreset ITEM_PRICE_INT_BACKGROUND = new ColorPreset(0.51f, 0.14f);

	@Getter
	@Setter
	private float clientHue = 0.65f; //TODO: сделать дефолтный хуе в нормальном месте

	public Color get(ColorPreset preset) {
		return preset.get(clientHue);
	}

	public Color get(ColorPreset preset, float opacity) {
		return ColorUtils.applyAlpha(preset.get(clientHue), opacity);
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.add("hue", clientHue);
		return json;
	}

	@Override
	public ColorManager fromJson(JsonObject json) {
		clientHue = json.getFloat("hue", 0.65f);
		return this;
	}

	@Override
	public String getId() {
		return "colors";
	}

	@Override
	public void reset() {
		clientHue = 0.65f; //TODO: сделать дефолтный хуе в нормальном месте
	}

	public static class ColorPreset {

		private final float saturation;
		private final float brightness;

		public ColorPreset(float saturation, float brightness) {
			this.saturation = saturation;
			this.brightness = brightness;
		}

		public Color get(float hue) {
			return Color.getHSBColor(hue, saturation, brightness);
		}

		public Color get(float hue, float saturationMultiplier, float brightnessMultiplier) {
			return Color.getHSBColor(
					hue,
					Math.clamp(saturation * saturationMultiplier, 0, 1),
					Math.clamp(brightness * brightnessMultiplier, 0, 1)
			);
		}
	}
}
