package com.perfect.client.functional.function.visuals.breakEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.mining.BreakColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

import java.awt.*;

@Getter
public class BreakEspConfig extends FunctionConfig<BreakESP> {

	// ————————————————————————————— RANGE ——————————————————————————————— //

	private final SettingGroup sgRange = settings.createGroup();

	private final DoubleSetting targetRange = sgRange.add(new DoubleSetting
			.Builder()
			.id("target-range")
			.defaultValue(20)
			.sliderRange(5, 50)
			.range(1, 300)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting miningRange = sgRange.add(new DoubleSetting
			.Builder()
			.id("mining-range")
			.defaultValue(6)
			.sliderRange(0, 7)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	// ————————————————————————————— MINING ——————————————————————————————— //

	private final SettingGroup sgMining = settings.createGroup();

	private final DoubleSetting speed = sgMining.add(new DoubleSetting
			.Builder()
			.id("speed")
			.defaultValue(1)
			.sliderRange(0.1, 1.5)
			.range(0.1, 5)
			.build());

	private final BoolSetting doubleMine = sgMining.add(new BoolSetting
			.Builder()
			.id("double-mine")
			.defaultValue(false)
			.build());

	// ————————————————————————————— RENDER ——————————————————————————————— //

	private final SettingGroup sgRender = settings.createGroup();

	private final BundleSetting<CosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.build());

	private final EnumSetting<BreakColor> colorMode = sgRender.add(new EnumSetting
			.Builder<>(BreakColor.values())
			.id("break-color-mode")
			.defaultValue(BreakColor.PROGRESS)
			.build());

	// ————————————————————————————— COLORS ——————————————————————————————— //

	private final SettingGroup sgColors = settings.createGroup();

	// PROGRESS

	private final ColorSetting startLineColor = sgColors.add(new ColorSetting
			.Builder()
			.id("break-start-line-color")
			.defaultValue(Color.RED)
			.visible(this::isProgressColor)
			.build());

	private final ColorSetting startSideColor = sgColors.add(new ColorSetting
			.Builder()
			.id("break-start-side-color")
			.defaultValue(new Color(255, 0, 0, 40))
			.visible(this::isProgressColor)
			.build());

	private final ColorSetting endLineColor = sgColors.add(new ColorSetting
			.Builder()
			.id("break-end-line-color")
			.defaultValue(Color.GREEN)
			.visible(this::isProgressColor)
			.build());

	private final ColorSetting endSideColor = sgColors.add(new ColorSetting
			.Builder()
			.id("break-end-side-color")
			.defaultValue(new Color(0, 255, 0, 40))
			.visible(this::isProgressColor)
			.build());

	// STATIC

	private final MultiColorSetting staticLineColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.visible(this::isStaticColor)
			.build());

	private final MultiColorSetting staticSideColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(this::isStaticColor)
			.build());

	// ——————————————————————————— ANIMATION ————————————————————————————— //

	private final SettingGroup sgAnimation = settings.createGroup();

	private final BoolSetting smoothAnimation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("break-smooth-animation")
			.defaultValue(true)
			.build());

	private final BoolSetting smoothColor = sgAnimation.add(new BoolSetting
			.Builder()
			.id("break-smooth-color")
			.defaultValue(true)
			.visible(this::isProgressColor)
			.build());

	private final DoubleSetting animation = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("break-animation")
			.defaultValue(1)
			.range(0.2, 5)
			.build());

	// ——————————————————————————————————————————————————————————————————— //

	public BreakEspConfig(BreakESP function) {
		super(function);
	}

	private boolean isProgressColor() {
		return colorMode.get(BreakColor.PROGRESS);
	}

	private boolean isStaticColor() {
		return colorMode.get(BreakColor.STATIC);
	}
}
