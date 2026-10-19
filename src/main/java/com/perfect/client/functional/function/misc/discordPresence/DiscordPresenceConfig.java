package com.perfect.client.functional.function.misc.discordPresence;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class DiscordPresenceConfig extends FunctionConfig<DiscordPresence> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<DiscordPresence.Type> smallImageMode = sgGeneral.add(new EnumSetting
			.Builder<>(DiscordPresence.Type.values())
			.id("small-image-mode")
			.defaultValue(DiscordPresence.Type.PLAYER_AVATAR)
			.build());

	public DiscordPresenceConfig(DiscordPresence function) {
		super(function);
	}
}
