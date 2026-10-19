package com.perfect.client.functional.function.combat.pistonPush;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class PistonPushConfig extends FunctionConfig<PistonPush> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgRenderEffects = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final DoubleSetting placeRange = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("place-range")
			.defaultValue(4.5)
			.sliderRange(1, 6)
			.range(0, 10)
			.suffix(Suffix.METERS)
			.build());

	private final IntSetting pushDelay = sgGeneral.add(new IntSetting
			.Builder()
			.id("push-delay")
			.defaultValue(500)
			.sliderRange(100, 1000)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting pushOnSelf = sgGeneral.add(new BoolSetting
			.Builder()
			.id("push-on-self")
			.defaultValue(true)
			.build());

	private final BoolSetting rotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(true)
			.build());

	private final BoolSetting holeFill = sgGeneral.add(new BoolSetting
			.Builder()
			.id("hole-fill")
			.defaultValue(true)
			.build());

	private final BoolSetting antiSelfPush = sgGeneral.add(new BoolSetting
			.Builder()
			.id("anti-self-push")
			.defaultValue(true)
			.build());

	private final BoolSetting autoToggle = sgGeneral.add(new BoolSetting
			.Builder()
			.id("auto-toggle")
			.defaultValue(true)
			.build());

	// —————————————————————————————— PAUSE ———————————————————————————————— //

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

	// ————————————————————————————— RENDER ———————————————————————————————— //

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final BundleSetting<AnimatedCosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<AnimatedCosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new AnimatedCosmeticsBundle())
			.build());

	private final MultiColorSetting lineColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.visible(render::get)
			.build());

	private final MultiColorSetting sideColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(render::get)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public PistonPushConfig(PistonPush function) {
		super(function);
	}

}
