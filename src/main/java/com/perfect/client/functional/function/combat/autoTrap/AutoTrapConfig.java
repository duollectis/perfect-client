package com.perfect.client.functional.function.combat.autoTrap;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.functions.Trap;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoTrapConfig extends PlaceFunctionConfig<AutoTrap> {

	private final SettingGroup sgAutoTrap = settings.createGroup(0);

	private final BoolSetting antiStep = sgAutoTrap.add(new BoolSetting
			.Builder()
			.id("anti-step")
			.defaultValue(false)
			.build());

	private final Setting<Trap> mode = sgAutoTrap.add(new EnumSetting
			.Builder<>(Trap.values())
			.id("mode")
			.defaultValue(Trap.HEAD)
			.build());

	private final DoubleSetting targetRange = sgAutoTrap.add(new DoubleSetting
			.Builder()
			.id("target-range")
			.sliderRange(0, 6)
			.min(0)
			.defaultValue(5)
			.suffix(Suffix.METERS)
			.build());

	public AutoTrapConfig(AutoTrap function) {
		super(function);
	}
}
