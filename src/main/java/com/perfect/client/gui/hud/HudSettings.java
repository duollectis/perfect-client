package com.perfect.client.gui.hud;

import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.Settings;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.ButtonSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;

@Getter

public class HudSettings {

	private final Settings settings = new Settings();
	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting enabled = sgGeneral.add(new BoolSetting
			.Builder()
			.id("enabled")
			.defaultValue(true)
			.build());

	private final DoubleSetting scale = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("hud-scale")
			.range(0.5, 4.0)
			.defaultValue(1.0)
			.build());

	private final DoubleSetting snapDistance = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("grid-snap-distance")
			.range(0.0, 3.0)
			.sliderRange(0.0, 1.5)
			.defaultValue(0.5)
			.build());

	private final MultiColorSetting textColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("text-color")
			.defaultValue(MultiColor.client())
			.build());

	private final DoubleSetting colorSpread = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("color-spread")
			.sliderRange(1.0, 100.0)
			.range(1.0, 100.0)
			.defaultValue(4.0)
			.visible(() -> textColor.get().getColorList().size() > 1)
			.build());

	private final ColorSetting valueTextColor = sgGeneral.add(new ColorSetting
			.Builder()
			.id("value-text-color")
			.defaultValue(Color.WHITE)
			.build());

	private final ColorSetting bracketsColor = sgGeneral.add(new ColorSetting
			.Builder()
			.id("brackets-color")
			.defaultValue(Color.GRAY)
			.build());

	private final ColorSetting backgroundColor = sgGeneral.add(new ColorSetting
			.Builder()
			.id("background-color")
			.defaultValue(new Color(0, 0, 0, 50))
			.build());

	private final ButtonSetting hudEditor = sgGeneral.add(ButtonSetting
			.builder()
			.name("hud-editor")
			.action(() -> {
				mc.setScreen(new HudEditorScreen());
			})
			.build());
}
