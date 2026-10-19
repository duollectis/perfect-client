package com.perfect.client.functional.function.combat.crawlTrap;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class CrawlTrapConfig extends PlaceFunctionConfig<CrawlTrap> {

	private final SettingGroup sgTrap = settings.createGroup(0);

	private final DoubleSetting trapRadius = sgTrap.add(new DoubleSetting
			.Builder()
			.id("trap-radius")
			.defaultValue(0.8)
			.range(0.5, 3)
			.suffix(Suffix.METERS)
			.build());

	public CrawlTrapConfig(CrawlTrap function) {
		super(function);
	}
}
