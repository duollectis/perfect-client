package com.perfect.client.functional.function.misc.autoWither;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoWitherConfig extends FunctionConfig<AutoWither> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgPlace = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(6)
			.sliderRange(0, 7)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	// —————————————————————————————— PLACE ———————————————————————————————— //

	private final EnumSetting<Swap> swap = sgPlace.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final IntSetting placeDelay = sgPlace.add(new IntSetting
			.Builder()
			.id("place-delay")
			.defaultValue(0)
			.range(0, 1000)
			.sliderRange(0, 300)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting onlyOnGround = sgPlace.add(new BoolSetting
			.Builder()
			.id("only-on-ground")
			.defaultValue(true)
			.build());

	// —————————————————————————————— PAUSE ———————————————————————————————— //

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(false)
			.build());

	private final BoolSetting pauseOnAutoExp = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-auto-exp")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-blink")
			.defaultValue(true)
			.build());

	// —————————————————————————————— RENDER ———————————————————————————————— //

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

	public AutoWitherConfig(AutoWither function) {
		super(function);
	}
}

