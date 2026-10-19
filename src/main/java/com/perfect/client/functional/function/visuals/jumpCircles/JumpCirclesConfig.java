package com.perfect.client.functional.function.visuals.jumpCircles;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class JumpCirclesConfig extends FunctionConfig<JumpCircles> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final DoubleSetting scale = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("scale")
			.defaultValue(80.0)
			.range(50.0, 200.0)
			.sliderRange(50.0, 200.0)
			.suffix(Suffix.PERCENT)
			.build());

	private final MultiColorSetting color = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("color")
			.defaultValue(MultiColor.client())
			.build());

	public JumpCirclesConfig(JumpCircles function) {
		super(function);
	}
}
