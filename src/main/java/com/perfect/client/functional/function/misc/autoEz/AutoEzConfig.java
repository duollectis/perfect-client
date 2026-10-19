package com.perfect.client.functional.function.misc.autoEz;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoEzConfig extends FunctionConfig<AutoEz> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgKills = settings.createGroup();

	private final DoubleSetting targetRange = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("target-range")
			.defaultValue(10)
			.sliderRange(5, 20)
			.range(1, 50)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting sendDelay = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("send-delay")
			.defaultValue(0.0)
			.range(0.0, 10.0)
			.sliderRange(0.0, 5.0)
			.suffix(Suffix.SECONDS)
			.build());

	private final EnumSetting<AutoEz.MessageMode> kills = sgKills.add(new EnumSetting
			.Builder<>(AutoEz.MessageMode.values())
			.id("kills")
			.defaultValue(AutoEz.MessageMode.Built_In)
			.build());

	private final StringSetting killsMessage = sgKills.add(new StringSetting
			.Builder()
			.id("kills-message")
			.defaultValue("<player> died after popping <pops> totem(s)!")
			.visible(() -> kills.get(AutoEz.MessageMode.Own))
			.build());

	public AutoEzConfig(AutoEz function) {
		super(function);
	}
}
