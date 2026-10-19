package com.perfect.client.functional.function.misc.fakePlayer;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import lombok.Getter;

@Getter
public class FakePlayerConfig extends FunctionConfig<FakePlayer> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final StringSetting nickname = sgGeneral.add(new StringSetting
			.Builder()
			.id("nickname")
			.defaultValue("nnner_shadow")
			.build());

	public FakePlayerConfig(FakePlayer function) {
		super(function);
	}
}
