package com.perfect.client.functional.function.movement.safeWalk;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import lombok.Getter;

@Getter
public class SafeWalkConfig extends FunctionConfig<SafeWalk> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<SafeWalk.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(SafeWalk.Mode.values())
			.id("mode")
			.defaultValue(SafeWalk.Mode.NORMAL)
			.onChangedMode(function::getModeContainer)
			.build());

	private final IntRangeSetting delay = sgGeneral.add(new IntRangeSetting
			.Builder()
			.id("delay")
			.defaultValue(1, 3)
			.range(0, 20)
			.sliderRange(0, 6)
			.visible(this::isLegitMode)
			.onChanged(function::updateSneakDelay)
			.build());

	public SafeWalkConfig(SafeWalk function) {
		super(function);
	}

	private boolean isLegitMode() {
		return mode.get(SafeWalk.Mode.LEGIT);
	}
}
