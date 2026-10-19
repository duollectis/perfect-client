package com.perfect.client.functional.function.visuals.blockEsp;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.list.BlockColorSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class BlockEspConfig extends FunctionConfig<BlockESP> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgOpacity = settings.createGroup();
	private final SettingGroup sgTracers = settings.createGroup();

	private final BlockColorSetting blocks = sgGeneral.add(new BlockColorSetting
			.Builder()
			.id("blocks")
			.onChanged(function::rescan)
			.build());

	private final BoolSetting tracers = sgTracers.add(new BoolSetting
			.Builder()
			.id("tracers")
			.defaultValue(false)
			.build());

	private final DoubleSetting tracerOpacity = sgTracers.add(new DoubleSetting
			.Builder()
			.id("tracer-opacity")
			.sliderRange(0.0, 100.0)
			.range(0.0, 100.0)
			.defaultValue(100.0)
			.suffix(Suffix.PERCENT)
			.visible(tracers::get)
			.build());

	private final DoubleSetting lineOpacity = sgOpacity.add(new DoubleSetting
			.Builder()
			.id("line-opacity")
			.sliderRange(0.0, 100.0)
			.range(0.0, 100.0)
			.defaultValue(100.0)
			.suffix(Suffix.PERCENT)
			.build());

	private final DoubleSetting sideOpacity = sgOpacity.add(new DoubleSetting
			.Builder()
			.id("side-opacity")
			.sliderRange(0.0, 100.0)
			.range(0.0, 100.0)
			.defaultValue(10.0)
			.suffix(Suffix.PERCENT)
			.build());

	public BlockEspConfig(BlockESP function) {
		super(function);
	}
}
