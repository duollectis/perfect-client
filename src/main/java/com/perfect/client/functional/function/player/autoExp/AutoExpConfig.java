package com.perfect.client.functional.function.player.autoExp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoExpConfig extends FunctionConfig<AutoExp> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgRotate = settings.createGroup();

	private final IntRangeSetting delay = sgGeneral.add(new IntRangeSetting
			.Builder()
			.id("delay")
			.defaultValue(0, 0)
			.sliderRange(0, 5)
			.suffix(Suffix.TICKS)
			.onChanged(function::getDelay)
			.build());

	private final IntSetting perTick = sgGeneral.add(new IntSetting
			.Builder()
			.id("per-tick")
			.defaultValue(1)
			.sliderRange(1, 10)
			.build());

	private final BoolSetting noUnnecessary = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-unnecessary")
			.defaultValue(true)
			.build());

	private final BoolSetting disableAfterMending = sgGeneral.add(new BoolSetting
			.Builder()
			.id("disable-after-mending")
			.defaultValue(true)
			.visible(noUnnecessary::get)
			.build());

	private final BoolSetting rotate = sgRotate.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(true)
			.build());

	private final DoubleSetting pitch = sgRotate.add(new DoubleSetting
			.Builder()
			.id("pitch")
			.defaultValue(75)
			.range(-90, 90)
			.sliderRange(-90, 90)
			.visible(rotate::get)
			.build());

	public AutoExpConfig(AutoExp function) {
		super(function);
	}
}
