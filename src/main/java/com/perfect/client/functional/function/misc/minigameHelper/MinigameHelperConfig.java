package com.perfect.client.functional.function.misc.minigameHelper;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class MinigameHelperConfig extends FunctionConfig<MinigameHelper> {

	private final SettingGroup sgTeam = settings.createGroup();

	private final BoolSetting teamFriends = sgTeam.add(new BoolSetting
			.Builder()
			.id("friend-team")
			.defaultValue(true)
			.build());

	private final BoolSetting teamColors = sgTeam.add(new BoolSetting
			.Builder()
			.id("team-colors")
			.defaultValue(true)
			.build());

	public MinigameHelperConfig(MinigameHelper function) {
		super(function);
	}
}
