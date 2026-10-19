package com.perfect.client.functional.function.combat.bowRelease;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BowReleaseConfig extends FunctionConfig<BowRelease> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting onlyRightClick = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-right-click")
			.defaultValue(false)
			.build());

	private final IntSetting charge = sgGeneral.add(new IntSetting
			.Builder()
			.id("charge-power")
			.defaultValue(5)
			.sliderRange(4, 20)
			.suffix(Suffix.TICKS)
			.build());

	public BowReleaseConfig(BowRelease function) {
		super(function);
	}
}
