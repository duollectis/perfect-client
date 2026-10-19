package com.perfect.client.functional.function.visuals.imageFilter;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import lombok.Getter;

import java.awt.*;

@Getter
public class ImageFilterConfig extends FunctionConfig<ImageFilter> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final ColorSetting color = sgGeneral.add(new ColorSetting
			.Builder()
			.id("color")
			.defaultValue(Color.WHITE)
			.build());

	public ImageFilterConfig(ImageFilter function) {
		super(function);
	}
}
