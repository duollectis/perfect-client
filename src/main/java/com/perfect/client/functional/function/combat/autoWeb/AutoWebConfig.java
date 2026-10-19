package com.perfect.client.functional.function.combat.autoWeb;

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
public class AutoWebConfig extends FunctionConfig<AutoWeb> {

	protected final SettingGroup sgGeneral = settings.createGroup();
	protected final SettingGroup sgRotate = settings.createGroup();
	protected final SettingGroup sgExtrapolation = settings.createGroup();
	protected final SettingGroup sgPause = settings.createGroup();
	protected final SettingGroup sgRender = settings.createGroup();

	private final EnumSetting<AutoWeb.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(AutoWeb.Mode.values())
			.id("mode")
			.defaultValue(AutoWeb.Mode.FEET)
			.build());

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final DoubleSetting placeRange = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting onlyOnGround = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-on-ground")
			.defaultValue(true)
			.build());

	private final BoolSetting noSelfWeb = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-self-web")
			.defaultValue(true)
			.build());

	private final IntSetting placeDelay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.sliderRange(0, 1000)
			.range(0, 3000)
			.defaultValue(0)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting rotate = sgRotate.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(true)
			.build());

	private final IntSetting extrapolation = sgExtrapolation.add(new IntSetting
			.Builder()
			.id("extrapolation")
			.sliderRange(0, 10)
			.range(0, 20)
			.defaultValue(2)
			.suffix(Suffix.TICKS)
			.build());

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(false)
			.build());

	private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-blink")
			.defaultValue(false)
			.build());

	private final BoolSetting pauseOnAutoExp = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-auto-exp")
			.defaultValue(false)
			.build());

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

	public AutoWebConfig(AutoWeb function) {
		super(function);
	}
}
