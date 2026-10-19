package com.perfect.client.functional.function.misc.betterTab;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import lombok.Getter;

@Getter
public class BetterTabConfig extends FunctionConfig<BetterTab> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgGroups = settings.createGroup();

	private final IntSetting tabSize = sgGeneral.add(new IntSetting
			.Builder()
			.id("tab-size")
			.range(1, 1000)
			.sliderRange(1, 200)
			.defaultValue(100)
			.toggleable(true)
			.build());

	private final BoolSetting playerHeads = sgGeneral.add(new BoolSetting
			.Builder()
			.id("player-heads")
			.defaultValue(true)
			.build());


	private final ColorSetting selfHighlight = sgGroups.add(new ColorSetting
			.Builder()
			.id("self-highlight")
			.defaultValue(MultiColor.client().getColor())
			.toggleable(true)
			.build());

	private final BoolSetting friendsHighlight = sgGroups.add(new BoolSetting
			.Builder()
			.id("friends-highlight")
			.defaultValue(true)
			.build());

	public BetterTabConfig(BetterTab function) {
		super(function);
	}
}
