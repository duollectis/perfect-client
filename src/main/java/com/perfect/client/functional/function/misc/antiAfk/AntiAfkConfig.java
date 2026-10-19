package com.perfect.client.functional.function.misc.antiAfk;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.range.DoubleRangeSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AntiAfkConfig extends FunctionConfig<AntiAFK> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumListSetting<AntiAFK.Actions> actions = sgGeneral.add(new EnumListSetting
			.Builder<>(AntiAFK.Actions.values())
			.id("actions")
			.defaultValue(AntiAFK.Actions.JUMP)
			.build());

	private final SettingGroup sgDelays = settings.createGroup();

	private final DoubleRangeSetting jumpDelay = sgDelays.add(new DoubleRangeSetting
			.Builder()
			.id("jump-delay")
			.sliderRange(0.0, 60.0)
			.defaultValue(3.0, 8.0)
			.visible(() -> actions.get(AntiAFK.Actions.JUMP))
			.suffix(Suffix.SECONDS)
			.onChanged(function::getJumpDelay)
			.build());

	public AntiAfkConfig(AntiAFK function) {
		super(function);
	}
}
