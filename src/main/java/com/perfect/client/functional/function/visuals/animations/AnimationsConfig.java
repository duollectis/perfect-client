package com.perfect.client.functional.function.visuals.animations;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AnimationsConfig extends FunctionConfig<Animations> {

	private final SettingGroup sgTotemAnimation = settings.createGroup();
	private final SettingGroup sgDeathAnimation = settings.createGroup();

	private final BoolSetting totemPopAnimation = sgTotemAnimation.add(new BoolSetting
			.Builder()
			.id("totem-pop-animation")
			.defaultValue(true)
			.onChanged(function::onToggled)
			.build());

	private final IntSetting totemPopAnimationTime = sgTotemAnimation.add(new IntSetting
			.Builder()
			.id("animation-time")
			.defaultValue(300)
			.sliderRange(150, 500)
			.suffix(Suffix.MILLISECONDS)
			.visible(totemPopAnimation::get)
			.build());

	private final BoolSetting totemPopWaveMode = sgTotemAnimation.add(new BoolSetting
			.Builder()
			.id("wave-mode")
			.defaultValue(true)
			.visible(totemPopAnimation::get)
			.build());

	private final BoolSetting ignoreSelf = sgTotemAnimation.add(new BoolSetting
			.Builder()
			.id("ignore-self")
			.defaultValue(false)
			.build());

	private final BoolSetting deathAnimation = sgDeathAnimation.add(new BoolSetting
			.Builder()
			.id("death-animation")
			.defaultValue(true)
			.onChanged(function::onToggled)
			.build());

	private final IntSetting deathAnimationTime = sgDeathAnimation.add(new IntSetting
			.Builder()
			.id("animation-time")
			.defaultValue(300)
			.sliderRange(150, 650)
			.suffix(Suffix.MILLISECONDS)
			.visible(deathAnimation::get)
			.build());

	private final BoolSetting deathWaveMode = sgDeathAnimation.add(new BoolSetting
			.Builder()
			.id("wave-mode")
			.defaultValue(true)
			.visible(deathAnimation::get)
			.build());

	private final MultiColorSetting deathAnimationLineColor = sgDeathAnimation.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.visible(deathAnimation::get)
			.build());

	private final MultiColorSetting deathAnimationSideColor = sgDeathAnimation.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(deathAnimation::get)
			.build());

	public AnimationsConfig(Animations function) {
		super(function);
	}
}
