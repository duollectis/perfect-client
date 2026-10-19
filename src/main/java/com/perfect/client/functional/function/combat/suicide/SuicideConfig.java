package com.perfect.client.functional.function.combat.suicide;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class SuicideConfig extends FunctionConfig<Suicide> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting toggleOnDeath = sgGeneral.add(new BoolSetting.Builder()
			.id("toggle-on-death")
			.defaultValue(true)
			.build());

	public SuicideConfig(Suicide function) {
		super(function);
	}
}
