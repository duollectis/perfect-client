package com.perfect.client.functional.function.misc.antiBot;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class AntiBotConfig extends FunctionConfig<AntiBot> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgModes = settings.createGroup();

	private final BoolSetting ping = sgModes.add(new BoolSetting
			.Builder()
			.id("ping")
			.onChanged(function::onToggled)
			.defaultValue(true)
			.build());

	private final BoolSetting nickname = sgModes.add(new BoolSetting
			.Builder()
			.id("nickname")
			.onChanged(function::onToggled)
			.defaultValue(true)
			.build());

	private final BoolSetting uuid = sgModes.add(new BoolSetting
			.Builder()
			.id("uuid")
			.onChanged(function::onToggled)
			.defaultValue(false)
			.build());

	private final BoolSetting movement = sgModes.add(new BoolSetting
			.Builder()
			.id("movement")
			.onChanged(function::onToggled)
			.defaultValue(false)
			.build());

	private final BoolSetting health = sgModes.add(new BoolSetting
			.Builder()
			.id("health")
			.onChanged(function::onToggled)
			.defaultValue(false)
			.build());

	private final BoolSetting age = sgModes.add(new BoolSetting
			.Builder()
			.id("age")
			.onChanged(function::onToggled)
			.defaultValue(false)
			.build());

	private final BoolSetting speed = sgModes.add(new BoolSetting
			.Builder()
			.id("speed")
			.onChanged(function::onToggled)
			.defaultValue(false)
			.build());

	public AntiBotConfig(AntiBot function) {
		super(function);
	}
}
