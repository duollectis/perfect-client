package com.perfect.client.functional.function.visuals.blockSelection;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BlockSelectionConfig extends FunctionConfig<BlockSelection> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAnimation = settings.createGroup();

	private final BundleSetting<CosmeticsBundle> cosmetics = sgGeneral.add(new BundleSetting
			.Builder<CosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new CosmeticsBundle())
			.build());

	private final MultiColorSetting lineColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("line-color")
			.defaultValue(MultiColor.client())
			.build());

	private final MultiColorSetting sideColor = sgGeneral.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.build());

	private final BoolSetting animation = sgAnimation.add(new BoolSetting
			.Builder()
			.id("animation")
			.defaultValue(true)
			.build());

	private final DoubleSetting animationSpeed = sgAnimation.add(new DoubleSetting
			.Builder()
			.id("speed")
			.range(5.0, 80.0)
			.sliderRange(5.0, 80.0)
			.defaultValue(20.0)
			.suffix(Suffix.PERCENT)
			.visible(animation::get)
			.build());

	public BlockSelectionConfig(BlockSelection function) {
		super(function);
	}
}
