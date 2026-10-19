package com.perfect.client.functional.function.player.autoKit;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.StringSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoKitConfig extends FunctionConfig<AutoKit> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgDoubleKit = settings.createGroup();

	private final StringSetting kitCommand = sgGeneral.add(new StringSetting
			.Builder()
			.id("kit-command")
			.defaultValue("/kit")
			.build());

	private final BoolSetting disable = sgGeneral.add(new BoolSetting
			.Builder()
			.id("disable")
			.defaultValue(false)
			.build());

	private final BoolSetting doubleKit = sgDoubleKit.add(new BoolSetting
			.Builder()
			.id("double-kit")
			.defaultValue(false)
			.build());

	private final StringSetting secondKitCommand = sgDoubleKit.add(new StringSetting
			.Builder()
			.id("second-kit-command")
			.defaultValue("/kit")
			.visible(doubleKit::get)
			.build());

	private final StringSetting killCommand = sgDoubleKit.add(new StringSetting
			.Builder()
			.id("kill-command")
			.defaultValue("/kill")
			.visible(doubleKit::get)
			.build());

	private final IntSetting delay = sgDoubleKit.add(new IntSetting
			.Builder()
			.id("delay")
			.defaultValue(10)
			.range(0, 40)
			.sliderRange(5, 20)
			.suffix(Suffix.TICKS)
			.visible(doubleKit::get)
			.build());

	public AutoKitConfig(AutoKit function) {
		super(function);
	}
}
