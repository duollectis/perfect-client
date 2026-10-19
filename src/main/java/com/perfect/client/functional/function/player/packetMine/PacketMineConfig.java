package com.perfect.client.functional.function.player.packetMine;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.functions.mining.BreakColor;
import com.perfect.client.core.utils.functions.mining.RebreakMode;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

import java.awt.*;

@Getter
public class PacketMineConfig extends FunctionConfig<com.perfect.client.functional.function.player.packetMine.PacketMine> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgMining = settings.createGroup();
	private final SettingGroup sgSwap = settings.createGroup();
	private final SettingGroup sgAttempts = settings.createGroup();
	private final SettingGroup sgPrediction = settings.createGroup();
	private final SettingGroup sgRebreak = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();
	private final SettingGroup sgAnimation = settings.createGroup();
	private final SettingGroup sgRenderEffects = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting rotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(true)
			.build());

	private final BoolSetting onlyOnGround = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-on-ground")
			.defaultValue(true)
			.build());

	// ————————————————————————————— MINING ——————————————————————————————— //

	private final DoubleSetting speed = sgMining.add(new DoubleSetting
			.Builder()
			.id("speed")
			.defaultValue(1)
			.sliderRange(0.1, 1.5)
			.range(0.1, 5)
			.build());

	private final BoolSetting ccSpeed = sgMining.add(new BoolSetting
			.Builder()
			.id("crystalpvp-cc")
			.defaultValue(false)
			.build());

	private final BoolSetting grim = sgMining.add(new BoolSetting
			.Builder()
			.id("grim")
			.defaultValue(false)
			.build());

	private final BoolSetting doubleMine = sgMining.add(new BoolSetting
			.Builder()
			.id("double-mine")
			.defaultValue(false)
			.onChanged(function::onDoubleMineConfig)
			.build());

	// ————————————————————————————— SWAP ——————————————————————————————— //

	private final EnumSetting<Swap> swap = sgSwap.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.ALTERNATIVE)
			.build());

	private final BoolSetting resetOnSwap = sgSwap.add(new BoolSetting
			.Builder()
			.id("reset-on-swap")
			.defaultValue(true)
			.build());

	private final BoolSetting newVersion = sgSwap.add(new BoolSetting
			.Builder()
			.id("new-version")
			.defaultValue(false)
			.build());

	private final IntRangeSetting swapTime = sgSwap.add(new IntRangeSetting
			.Builder()
			.id("swap-time")
			.defaultValue(-1, 1)
			.sliderRange(-3, 3)
			.range(-20, 20)
			.visible(this::isLongSwap)
			.build());

	// ————————————————————————————— PREDICTION ——————————————————————————————— //

	private final BoolSetting clientBreak = sgPrediction.add(new BoolSetting
			.Builder()
			.id("client-break")
			.defaultValue(true)
			.build());

	// ————————————————————————————— REBREAK ——————————————————————————————— //

	public final EnumSetting<RebreakMode> rebreak = sgRebreak.add(new EnumSetting
			.Builder<>(RebreakMode.values())
			.id("rebreak")
			.defaultValue(RebreakMode.FAST)
			.build());

	// ————————————————————————————— PAUSE ——————————————————————————————— //

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-blink")
			.defaultValue(true)
			.build());

	// ————————————————————————————— RENDER ——————————————————————————————— //

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final BoolSetting renderRebreak = sgRender.add(new BoolSetting
			.Builder()
			.id("render-rebreak")
			.defaultValue(true)
			.visible(render::get)
			.build());

	private final BundleSetting<AnimatedCosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<AnimatedCosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new AnimatedCosmeticsBundle())
			.visible(render::get)
			.build());

	private final EnumSetting<BreakColor> colorMode = sgRender.add(new EnumSetting
			.Builder<>(BreakColor.values())
			.id("break-color-mode")
			.defaultValue(BreakColor.PROGRESS)
			.visible(render::get)
			.build());

	// ————————————————————————————— COLORS ——————————————————————————————— //

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

	private final BoolSetting smoothAnimation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("break-smooth-animation")
			.defaultValue(true)
			.visible(render::get)
			.build());

	private final BoolSetting smoothColor = sgAnimation.add(new BoolSetting
			.Builder()
			.id("break-smooth-color")
			.defaultValue(true)
			.visible(render::get)
			.build());

	private final DoubleSetting animation = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("break-animation")
			.defaultValue(1)
			.range(0.2, 5)
			.visible(render::get)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public PacketMineConfig(com.perfect.client.functional.function.player.packetMine.PacketMine function) {
		super(function);
	}

	private boolean isProgressColor() {
		return render.get() && colorMode.get(BreakColor.PROGRESS);
	}

	private boolean isStaticColor() {
		return render.get() && colorMode.get(BreakColor.STATIC);
	}

	private boolean isLongSwap() {
		return newVersion.get() || doubleMine.get();
	}
}
