package com.perfect.client.functional.function.combat.tntAura;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class TntAuraConfig extends PlaceFunctionConfig<TntAura> {

	private final SettingGroup sgGeneral = settings.createGroup(0);
	private final SettingGroup sgTntTrap = settings.createGroup(1);

	private final IntSetting tntDelay = sgGeneral.add(new IntSetting
			.Builder()
			.id("tnt-delay")
			.defaultValue(0)
			.sliderRange(0, 200)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting tntSpam = sgGeneral.add(new BoolSetting
			.Builder()
			.id("tnt-spam")
			.defaultValue(false)
			.build());

	private final BoolSetting onlyTargetsInHole = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-targets-in-hole")
			.defaultValue(true)
			.build());

	private final EnumSetting<TntAura.TntTrap> tntTrap = sgTntTrap.add(new EnumSetting
			.Builder<>(TntAura.TntTrap.values())
			.id("trap")
			.defaultValue(TntAura.TntTrap.NONE)
			.build());

	private final BoolSetting faceTrap = sgGeneral.add(new BoolSetting
			.Builder()
			.id("face-trap")
			.defaultValue(false)
			.build());

	private final BoolSetting onlyTopTrapped = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-top-trapped")
			.defaultValue(true)
			.visible(() -> tntTrap.get(TntAura.TntTrap.NONE))
			.build());

	private final BoolSetting breakTrap = sgTntTrap.add(new BoolSetting
			.Builder()
			.id("break-trap")
			.defaultValue(true)
			.build());

	public TntAuraConfig(TntAura function) {
		super(function);
	}

}
