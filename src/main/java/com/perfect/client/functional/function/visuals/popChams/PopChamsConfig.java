package com.perfect.client.functional.function.visuals.popChams;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class PopChamsConfig extends FunctionConfig<PopChams> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();

	private final DoubleSetting renderTime = sgGeneral.add(new DoubleSetting.Builder()
			.id("render-ticks")
			.range(0.0, 10.0)
			.sliderRange(0.0, 10.0)
			.defaultValue(5.0)
			.suffix(Suffix.SECONDS)
			.build());

	private final IntSetting maxChamsCount = sgGeneral.add(new IntSetting.Builder()
			.id("max-chams-count")
			.range(1, 100)
			.sliderRange(1, 10)
			.defaultValue(3)
			.build());

	private final DoubleSetting changeY = sgGeneral.add(new DoubleSetting.Builder()
			.id("change-y")
			.range(-10.0, 10.0)
			.sliderRange(-10.0, 10.0)
			.defaultValue(0.0)
			.toggleable(true)
			.build());

	private final DoubleSetting changeScale = sgGeneral.add(new DoubleSetting.Builder()
			.id("change-scale")
			.range(0.0, 3.0)
			.sliderRange(0.0, 3.0)
			.defaultValue(1.0)
			.toggleable(true)
			.build());

	private final DoubleSetting changeRotate = sgGeneral.add(new DoubleSetting.Builder()
			.id("change-rotate")
			.range(-180.0, 180.0)
			.sliderRange(-10.0, 10.0)
			.defaultValue(10.0)
			.toggleable(true)
			.build());

	private final BoolSetting ignoreFriends = sgRender.add(new BoolSetting
			.Builder()
			.id("ignore-friends")
			.defaultValue(true)
			.build());

	private final BoolSetting ignoreSelf = sgRender.add(new BoolSetting
			.Builder()
			.id("ignore-self")
			.defaultValue(true)
			.build());

	private final BoolSetting fadeOut = sgRender.add(new BoolSetting
			.Builder()
			.id("fade-out")
			.defaultValue(true)
			.build());

	private final MultiColorSetting lineColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting fillColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	public PopChamsConfig(PopChams function) {
		super(function);
	}
}
