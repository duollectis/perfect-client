package com.perfect.client.functional.function.visuals.holeEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

import java.awt.*;

@Getter
public class HoleEspConfig extends FunctionConfig<HoleESP> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgShape = settings.createGroup();
	private final SettingGroup sgObsidian = settings.createGroup();
	private final SettingGroup sgBedrock = settings.createGroup();
	private final SettingGroup sgAnimation = settings.createGroup();

	private final DoubleSetting rangeXZ = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range-xz")
			.defaultValue(10)
			.range(0, 100)
			.sliderRange(3, 15)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting rangeY = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range-y")
			.defaultValue(6)
			.range(0, 50)
			.sliderRange(1, 10)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting renderHeight = sgShape.add(new DoubleSetting
			.Builder()
			.id("render-height")
			.defaultValue(0.3)
			.range(-10, 10)
			.sliderRange(-1, 1)
			.build());

	private final BoolSetting gradient = sgShape.add(new BoolSetting
			.Builder()
			.id("gradient")
			.defaultValue(true)
			.build());

	private final BundleSetting<CosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.build());

	private final MultiColorSetting obsidianLineColor = sgObsidian.add(new MultiColorSetting
			.Builder()
			.id("obsidian-line")
			.defaultValue(new MultiColor(new Color(255, 0, 0, 100)))
			.build());

	private final MultiColorSetting obsidianSideColor = sgObsidian.add(new MultiColorSetting
			.Builder()
			.id("obsidian-side")
			.defaultValue(new MultiColor(new Color(255, 0, 0, 26)))
			.build());

	private final MultiColorSetting bedrockLineColor = sgBedrock.add(new MultiColorSetting
			.Builder()
			.id("bedrock-line")
			.defaultValue(new MultiColor(new Color(41, 246, 0, 100)))
			.build());

	private final MultiColorSetting bedrockSideColor = sgBedrock.add(new MultiColorSetting
			.Builder()
			.id("bedrock-side")
			.defaultValue(new MultiColor(new Color(41, 246, 0, 26)))
			.build());

	private final BoolSetting animation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("animation")
			.defaultValue(true)
			.build());

	private final DoubleSetting animationRadius = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("animation-radius")
			.range(0, 100)
			.sliderRange(0, 50)
			.defaultValue(30)
			.suffix(Suffix.PERCENT)
			.visible(animation::get)
			.build());

	private final DoubleSetting animationOffset = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("animation-offset")
			.range(-20, 20)
			.sliderRange(-10, 10)
			.defaultValue(-5)
			.visible(animation::get)
			.build());

	public HoleEspConfig(HoleESP function) {
		super(function);
	}
}
