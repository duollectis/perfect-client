package com.perfect.client.functional.function.movement.noSlow;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class NoSlowConfig extends FunctionConfig<NoSlow> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgNoSlow = settings.createGroup();

	private final EnumSetting<NoSlow.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(NoSlow.Mode.values())
			.id("mode")
			.onChangedMode(function::getModeContainer)
			.defaultValue(NoSlow.Mode.VANILLA)
			.build());

	private final BoolSetting itemUse = sgNoSlow.add(new BoolSetting
			.Builder()
			.id("item-use")
			.defaultValue(true)
			.build());

	private final BoolSetting shields = sgNoSlow.add(new BoolSetting
			.Builder()
			.id("shields")
			.defaultValue(false)
			.build());

	private final BoolSetting sneak = sgNoSlow.add(new BoolSetting
			.Builder()
			.id("sneak")
			.defaultValue(false)
			.build());

	private final BoolSetting crawl = sgNoSlow.add(new BoolSetting
			.Builder()
			.id("crawl")
			.defaultValue(false)
			.build());

	public NoSlowConfig(NoSlow function) {
		super(function);
	}
}
