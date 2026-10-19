package com.perfect.client.functional.function.visuals.arrows;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class ArrowsConfig extends FunctionConfig<Arrows> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAnimation = settings.createGroup();
	private final SettingGroup sgColor = settings.createGroup();

	private final DoubleSetting scale = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("scale")
			.range(5, 20)
			.sliderRange(5, 15)
			.defaultValue(8)
			.build());

	private final DoubleSetting radius = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("radius")
			.range(5, 100)
			.sliderRange(20, 100)
			.defaultValue(50)
			.build());

	private final DoubleSetting animationTime = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("animation-time")
			.range(0, 100)
			.sliderRange(0, 100)
			.defaultValue(50)
			.suffix(Suffix.PERCENT)
			.onChanged(i -> function.reset())
			.build());

	private final DoubleSetting animationOffset = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("animation-offset")
			.range(0, 80)
			.sliderRange(0, 80)
			.defaultValue(20)
			.onChanged(i -> function.reset())
			.build());

	private final MultiColorSetting color = sgColor.add(new MultiColorSetting
			.Builder()
			.id("color")
			.defaultValue(MultiColor.client())
			.build());

	public ArrowsConfig(Arrows function) {
		super(function);
	}
}
