package com.perfect.client.functional.function.misc.notifications;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class NotificationsConfig extends FunctionConfig<Notifications> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting ignoreSelf = sgGeneral.add(new BoolSetting
			.Builder()
			.id("ignore-self")
			.defaultValue(true)
			.build());

	private final BoolSetting totems = sgGeneral.add(new BoolSetting
			.Builder()
			.id("totems")
			.defaultValue(true)
			.build());

	public NotificationsConfig(Notifications function) {
		super(function);
	}
}
