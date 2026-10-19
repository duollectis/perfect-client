package com.perfect.client.functional.function.combat.maceKill;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import lombok.Getter;

@Getter
public class MaceKillConfig extends FunctionConfig<MaceKill> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final DoubleSetting fallHeight = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("fall-height")
			.range(1, 170)
			.sliderRange(1, 170)
			.defaultValue(22)
			.build());

	private final BoolSetting preventDeath = sgGeneral.add(new BoolSetting
			.Builder()
			.id("prevent-death")
			.defaultValue(true)
			.build());

	private final BoolSetting pauseOnBlocked = sgGeneral.add(new BoolSetting
			.Builder()
			.id("pause-on-blocked")
			.defaultValue(true)
			.build());

	public MaceKillConfig(MaceKill function) {
		super(function);
	}
}
