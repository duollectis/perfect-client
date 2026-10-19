package com.perfect.client.functional.function.player.infiniteElytra;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class InfiniteElytraConfig extends FunctionConfig<InfiniteElytra> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final Setting<Integer> switchDelay = sgGeneral.add(new IntSetting.Builder()
			.id("switch-delay")
			.suffix(Suffix.MILLISECONDS)
			.defaultValue(600)
			.sliderRange(100, 950)
			.build());

	private final Setting<Boolean> silent = sgGeneral.add(new BoolSetting
			.Builder()
			.id("silent")
			.defaultValue(true)
			.build());

	private final Setting<Boolean> pauseUnderCeiling = sgGeneral.add(new BoolSetting
			.Builder()
			.id("pause-under-ceiling")
			.defaultValue(false)
			.build());

	private final Setting<Integer> extrapolation = sgGeneral.add(new IntSetting.Builder()
			.id("extrapolation")
			.suffix(Suffix.TICKS)
			.defaultValue(5)
			.range(0, 100)
			.sliderRange(0, 10)
			.build());

	public InfiniteElytraConfig(InfiniteElytra function) {
		super(function);
	}
}
