package com.perfect.client.functional.function.misc.pingSpoof;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class PingSpoofConfig extends FunctionConfig<PingSpoof> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final Setting<Integer> delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("packet-lag")
			.range(0, 60000)
			.sliderRange(50, 1000)
			.defaultValue(300)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final Setting<Boolean> correction = sgGeneral.add(new BoolSetting
			.Builder()
			.id("correction")
			.defaultValue(true)
			.build());

	public PingSpoofConfig(PingSpoof function) {
		super(function);
	}
}
