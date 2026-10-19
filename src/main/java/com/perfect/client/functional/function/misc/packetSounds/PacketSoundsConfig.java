package com.perfect.client.functional.function.misc.packetSounds;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class PacketSoundsConfig extends FunctionConfig<PacketSounds> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final Setting<Boolean> placeSound = sgGeneral.add(new BoolSetting
			.Builder()
			.id("place-sound")
			.defaultValue(true)
			.build());

	private final Setting<Boolean> breakSound = sgGeneral.add(new BoolSetting
			.Builder()
			.id("break-sound")
			.defaultValue(true)
			.build());

	public PacketSoundsConfig(PacketSounds function) {
		super(function);
	}
}
