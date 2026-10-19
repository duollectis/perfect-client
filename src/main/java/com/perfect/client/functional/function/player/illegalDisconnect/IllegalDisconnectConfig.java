package com.perfect.client.functional.function.player.illegalDisconnect;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class IllegalDisconnectConfig extends FunctionConfig<IllegalDisconnect> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<IllegalDisconnect.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(IllegalDisconnect.Mode.values())
			.id("mode")
			.defaultValue(IllegalDisconnect.Mode.INVENTORY)
			.build());

	private final BoolSetting leave = sgGeneral.add(new BoolSetting
			.Builder()
			.id("leave")
			.defaultValue(true)
			.build());

	public IllegalDisconnectConfig(IllegalDisconnect function) {
		super(function);
	}
}
