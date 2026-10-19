package com.perfect.client.functional.function.visuals.fullBright;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class FullBrightConfig extends FunctionConfig<FullBright> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<FullBright.FullBrightMode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(FullBright.FullBrightMode.values())
			.id("mode")
			.defaultValue(FullBright.FullBrightMode.LUMINANCE)
			.onChanged(function::reloadMode)
			.build());

	private final EnumListSetting<FullBright.LightType> lightType = sgGeneral.add(new EnumListSetting
			.Builder<>(FullBright.LightType.values())
			.id("light-type")
			.defaultValue(FullBright.LightType.BLOCK)
			.visible(() -> mode.get() == FullBright.FullBrightMode.LUMINANCE)
			.onChanged(function::reload)
			.build());

	private final DoubleSetting skyBrightness = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("sky-brightness")
			.visible(this::isSkyLuminance)
			.defaultValue(80)
			.range(0, 100)
			.sliderMax(100)
			.onChanged(function::reload)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting blockBrightness = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("block-brightness")
			.visible(this::isBlockLuminance)
			.defaultValue(80)
			.range(0, 100)
			.sliderMax(100)
			.onChanged(function::reload)
			.suffix(Suffix.PERCENT)
			.build());

	public FullBrightConfig(FullBright function) {
		super(function);
	}

	public boolean isSkyLuminance() {
		return isLuminance() && lightType.get(FullBright.LightType.SKY);
	}

	public boolean isBlockLuminance() {
		return isLuminance() && lightType.get(FullBright.LightType.BLOCK);
	}

	public boolean isLuminance() {
		return mode.is(FullBright.FullBrightMode.LUMINANCE);
	}
}
