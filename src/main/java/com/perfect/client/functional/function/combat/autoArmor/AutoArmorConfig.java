package com.perfect.client.functional.function.combat.autoArmor;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoArmorConfig extends FunctionConfig<AutoArmor> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final IntSetting delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.defaultValue(5)
			.sliderRange(0, 20)
			.suffix(Suffix.TICKS)
			.build());

	private final BoolSetting ignoreElytra = sgGeneral.add(new BoolSetting
			.Builder()
			.id("ignore-elytra")
			.defaultValue(false)
			.build());

	public AutoArmorConfig(AutoArmor function) {
		super(function);
	}
}
