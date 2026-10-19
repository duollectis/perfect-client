package com.perfect.client.functional.function.visuals.fieldOfView;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import lombok.Getter;

@Getter
public class FieldOfViewConfig extends FunctionConfig<FieldOfView> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final IntSetting value = sgGeneral.add(new IntSetting
			.Builder()
			.id("value")
			.defaultValue(120)
			.range(1, 175)
			.sliderRange(1, 175)
			.build());

	public FieldOfViewConfig(FieldOfView function) {
		super(function);
	}
}
