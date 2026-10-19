package com.perfect.client.functional.function.player.selfBow;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class SelfBowConfig extends FunctionConfig<SelfBow> {

	private final SettingGroup sgEffects = settings.createGroup();
	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumListSetting<SelfBow.Effects> effects = sgEffects.add(new EnumListSetting
			.Builder<>(SelfBow.Effects.values())
			.id("effects")
			.defaultValue(SelfBow.Effects.values())
			.build());

	private final BoolSetting checkEffects = sgEffects.add(new BoolSetting
			.Builder()
			.id("check-effects")
			.defaultValue(false)
			.build());


	private final IntSetting delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.defaultValue(10)
			.sliderRange(0, 20)
			.suffix(Suffix.TICKS)
			.build());

	private final IntSetting charge = sgGeneral.add(new IntSetting
			.Builder()
			.id("charge-power")
			.defaultValue(5)
			.sliderRange(4, 20)
			.suffix(Suffix.TICKS)
			.build());

	private final IntSetting minHealth = sgGeneral.add(new IntSetting
			.Builder()
			.id("min-health")
			.defaultValue(10)
			.sliderRange(0, 36)
			.build());

	public SelfBowConfig(SelfBow function) {
		super(function);
	}
}
