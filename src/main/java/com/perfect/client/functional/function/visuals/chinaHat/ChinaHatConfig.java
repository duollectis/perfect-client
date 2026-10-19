package com.perfect.client.functional.function.visuals.chinaHat;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

@Getter
public class ChinaHatConfig extends FunctionConfig<ChinaHat> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgColors = settings.createGroup();

	private final BoolSetting friends = sgGeneral.add(new BoolSetting
			.Builder()
			.id("friends")
			.build());

	private final MultiColorSetting outlineColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("outline-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting fillColor = sgColors.add(new MultiColorSetting
			.Builder()
			.id("fill-color")
			.defaultValue(MultiColor.client(0.8f))
			.build());

	public ChinaHatConfig(ChinaHat function) {
		super(function);
	}
}
