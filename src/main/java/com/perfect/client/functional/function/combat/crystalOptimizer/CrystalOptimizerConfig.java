package com.perfect.client.functional.function.combat.crystalOptimizer;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class CrystalOptimizerConfig extends FunctionConfig<CrystalOptimizer> {

	private final SettingGroup sgGeneral = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final BoolSetting setDead = sgGeneral.add(new BoolSetting
			.Builder()
			.id("instant-remove")
			.defaultValue(true)
			.build());

	private final BoolSetting multiTask = sgGeneral.add(new BoolSetting
			.Builder()
			.id("multi-task")
			.defaultValue(true)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public CrystalOptimizerConfig(CrystalOptimizer function) {
		super(function);
	}
}
