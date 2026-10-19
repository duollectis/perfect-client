package com.perfect.client.functional.function.misc.spammer;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.number.range.DoubleRangeSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class SpammerConfig extends FunctionConfig<Spammer> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final DoubleRangeSetting delay = sgGeneral.add(new DoubleRangeSetting
			.Builder()
			.id("delay")
			.defaultValue(0.5, 1)
			.min(0)
			.suffix(Suffix.SECONDS)
			.onChanged(function::getDelay)
			.build());

	public SpammerConfig(Spammer function) {
		super(function);
	}
}
