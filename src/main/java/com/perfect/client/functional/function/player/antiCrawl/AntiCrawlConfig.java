package com.perfect.client.functional.function.player.antiCrawl;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class AntiCrawlConfig extends FunctionConfig<AntiCrawl> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting singleBlock = sgGeneral.add(new BoolSetting
			.Builder()
			.id("single-block")
			.defaultValue(false)
			.build());

	public AntiCrawlConfig(AntiCrawl function) {
		super(function);
	}
}
