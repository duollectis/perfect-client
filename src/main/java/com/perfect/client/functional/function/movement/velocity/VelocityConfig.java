package com.perfect.client.functional.function.movement.velocity;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class VelocityConfig extends FunctionConfig<Velocity> {

	private final SettingGroup sgMode = settings.createGroup();
	private final SettingGroup sgAntiCheat = settings.createGroup();
	private final SettingGroup sgVelocity = settings.createGroup();

	private final EnumSetting<Velocity.Mode> mode = sgMode.add(new EnumSetting
			.Builder<>(Velocity.Mode.values())
			.id("mode")
			.defaultValue(Velocity.Mode.NORMAL)
			.onChangedMode(function::getModeHandler)
			.build());

	private final BoolSetting knockback = sgVelocity.add(new BoolSetting
			.Builder()
			.id("knockback")
			.defaultValue(false)
			.build());

	private final BoolSetting explosions = sgVelocity.add(new BoolSetting
			.Builder()
			.id("explosions")
			.defaultValue(false)
			.build());

	private final BoolSetting entities = sgVelocity.add(new BoolSetting
			.Builder()
			.id("entities")
			.defaultValue(false)
			.build());

	private final BoolSetting blocks = sgVelocity.add(new BoolSetting
			.Builder()
			.id("blocks")
			.defaultValue(false)
			.build());

	private final BoolSetting fluids = sgVelocity.add(new BoolSetting
			.Builder()
			.id("fluids")
			.defaultValue(false)
			.build());

	private final BoolSetting fishhook = sgVelocity.add(new BoolSetting
			.Builder()
			.id("fishhook")
			.defaultValue(false)
			.build());

	public VelocityConfig(Velocity function) {
		super(function);
	}
}
