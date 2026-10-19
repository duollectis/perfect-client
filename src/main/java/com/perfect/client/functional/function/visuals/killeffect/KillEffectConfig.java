package com.perfect.client.functional.function.visuals.killeffect;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class KillEffectConfig extends FunctionConfig<KillEffect> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final EnumSetting<KillEffect.EffectType> killEffectType = sgGeneral.add(new EnumSetting
			.Builder<>(KillEffect.EffectType.values())
			.id("effect-type")
			.onChangedMode(function::getModeHandler)
			.build());

	public KillEffectConfig(KillEffect function) {
		super(function);
	}
}
