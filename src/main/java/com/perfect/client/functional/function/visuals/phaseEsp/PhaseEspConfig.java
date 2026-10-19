package com.perfect.client.functional.function.visuals.phaseEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class PhaseEspConfig extends FunctionConfig<PhaseESP> {

	private final SettingGroup sgHeight = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();

	private final DoubleSetting renderHeight = sgHeight.add(new DoubleSetting
			.Builder()
			.id("height")
			.defaultValue(0.2)
			.range(-1, 1)
			.suffix(Suffix.METERS)
			.build());

	private final BundleSetting<CosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.build());

	private final MultiColorSetting lineColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting fillColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	public PhaseEspConfig(PhaseESP function) {
		super(function);
	}
}
