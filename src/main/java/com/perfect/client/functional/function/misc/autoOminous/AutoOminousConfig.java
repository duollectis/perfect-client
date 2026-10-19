package com.perfect.client.functional.function.misc.autoOminous;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.ButtonSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

import java.awt.*;

@Getter
public class AutoOminousConfig extends FunctionConfig<AutoOminous> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgSwap = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();

	private final DoubleSetting triggerRange = sgGeneral.add(new DoubleSetting.Builder()
			.id("range")
			.suffix(Suffix.METERS)
			.defaultValue(2)
			.min(0)
			.sliderRange(1, 10)
			.build());

	private final ButtonSetting setupButton = sgGeneral.add(ButtonSetting
			.builder()
			.name("setup-position")
			.action(function::updateFarmPos)
			.build());

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final ColorSetting activeColor = sgColors.add(new ColorSetting
            .Builder()
			.id("active-color")
			.visible(render::get)
			.defaultValue(new Color(84, 255, 160))
			.build());

	private final ColorSetting inactiveColor = sgColors.add(new ColorSetting
			.Builder()
			.id("inactive-color")
			.visible(render::get)
			.defaultValue(new Color(80, 128, 255))
			.build());

	public AutoOminousConfig(AutoOminous function) {
		super(function);
	}
}
