package com.perfect.client.functional.function.visuals.nameProtect;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import lombok.Getter;

@Getter
public class NameProtectConfig extends FunctionConfig<NameProtect> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final StringSetting name = sgGeneral.add(new StringSetting
			.Builder()
			.id("nickname")
			.defaultValue("perfect-client")
			.build());

	private final BoolSetting friends = sgGeneral.add(new BoolSetting
			.Builder()
			.id("friends")
			.defaultValue(false)
			.build());

	public NameProtectConfig(NameProtect function) {
		super(function);
	}
}
