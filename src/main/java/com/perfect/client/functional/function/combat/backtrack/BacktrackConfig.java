package com.perfect.client.functional.function.combat.backtrack;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.CosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BacktrackConfig extends FunctionConfig<Backtrack> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgTicks = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(3)
			.sliderRange(1, 7)
			.range(0, 50)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting onlyPlayers = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-players")
			.defaultValue(true)
			.build());

	private final IntSetting backtrackTicks = sgTicks.add(new IntSetting
			.Builder()
			.id("backtrack-ticks")
			.range(1, 100)
			.sliderRange(1, 5)
			.defaultValue(1)
			.build());

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
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
			.visible(render::get)
			.build());

	private final MultiColorSetting sideColor = sgRender.add(new MultiColorSetting
			.Builder()
			.id("side-color")
			.defaultValue(MultiColor.clientFill())
			.visible(render::get)
			.build());

	public BacktrackConfig(Backtrack function) {
		super(function);
	}
}
