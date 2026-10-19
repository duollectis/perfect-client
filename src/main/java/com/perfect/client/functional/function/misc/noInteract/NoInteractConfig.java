package com.perfect.client.functional.function.misc.noInteract;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class NoInteractConfig extends FunctionConfig<NoInteract> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgHand = settings.createGroup();

	private final BoolSetting eat = sgGeneral.add(new BoolSetting
			.Builder()
			.id("eat")
			.defaultValue(true)
			.build());

	private final BoolSetting xp = sgGeneral.add(new BoolSetting
			.Builder()
			.id("xp-bottles")
			.defaultValue(true)
			.build());

	private final BoolSetting mainHand = sgHand.add(new BoolSetting
			.Builder()
			.id("main-hand")
			.defaultValue(true)
			.build());

	private final BoolSetting offHand = sgHand.add(new BoolSetting
			.Builder()
			.id("off-hand")
			.defaultValue(true)
			.build());

	public NoInteractConfig(NoInteract function) {
		super(function);
	}
}
