package com.perfect.client.functional.function.player.hitboxDesync;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.utils.text.filter.TextFilters;
import lombok.Getter;

@Getter
public class HitboxDesyncConfig extends FunctionConfig<HitboxDesync> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgOffset = settings.createGroup();

	private final EnumSetting<HitboxDesync.Logic> logic = sgGeneral.add(new EnumSetting
			.Builder<>(HitboxDesync.Logic.values())
			.id("logic")
			.defaultValue(HitboxDesync.Logic.LOOK)
			.build());

	private final StringSetting offset = sgOffset.add(new StringSetting
			.Builder()
			.id("offset")
			.defaultValue("0")
			.filter(TextFilters.DOUBLE)
			.build());

	public HitboxDesyncConfig(HitboxDesync function) {
		super(function);
	}
}
