package com.perfect.client.functional.function.combat.aimBot;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import lombok.Getter;

@Getter
public class AimBotConfig extends FunctionConfig<AimBot> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<AimBot.Rotate> rotate = sgGeneral.add(new EnumSetting
			.Builder<>(AimBot.Rotate.values())
			.id("rotate")
			.defaultValue(AimBot.Rotate.VANILLA)
			.build());

	private final IntSetting extrapolation = sgGeneral.add(new IntSetting
			.Builder()
			.id("extrapolation")
			.range(0, 20)
			.sliderRange(0, 10)
			.build());

	public AimBotConfig(AimBot function) {
		super(function);
	}
}
