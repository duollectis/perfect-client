package com.perfect.client.functional.function.movement.phase;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.functional.function.movement.phase.modes.PearlMode;
import lombok.Getter;

@Getter
public class PhaseConfig extends FunctionConfig<Phase> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgPearlGeneral = settings.createGroup();
	private final SettingGroup sgPearlRotate = settings.createGroup();
	private final SettingGroup sgPearlBypass = settings.createGroup();
	private final SettingGroup sgPearlAntiEntity = settings.createGroup();

	private final EnumSetting<Phase.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(Phase.Mode.values())
			.id("mode")
			.defaultValue(Phase.Mode.PEARL)
			.onChangedMode(function::getModeHandler)
			.build());

	private final EnumSetting<Swap> swap = sgPearlGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.visible(this::isPearlMode)
			.build());

	private final BoolSetting smart = sgPearlRotate.add(new BoolSetting
			.Builder()
			.id("smart")
			.defaultValue(true)
			.visible(this::isPearlMode)
			.build());

	private final EnumSetting<PearlMode.RotateMode> rotate = sgPearlRotate.add(new EnumSetting
			.Builder<>(PearlMode.RotateMode.values())
			.id("rotate")
			.defaultValue(PearlMode.RotateMode.VANILLA)
			.visible(this::isPearlMode)
			.build());

	private final BoolSetting antiPearlRotate = sgPearlRotate.add(new BoolSetting
			.Builder()
			.id("anti-pearl-rotate")
			.defaultValue(false)
			.visible(this::isPearlMode)
			.build());

	private final EnumSetting<PearlMode.Bypass> bypass = sgPearlBypass.add(new EnumSetting
			.Builder<>(PearlMode.Bypass.values())
			.id("bypass")
			.defaultValue(PearlMode.Bypass.NONE)
			.visible(this::isPearlMode)
			.build());

	private final BoolSetting antiEntity = sgPearlAntiEntity.add(new BoolSetting
			.Builder()
			.id("break-entities")
			.defaultValue(true)
			.visible(this::isPearlMode)
			.build());

	private final BoolSetting antiScaffolding = sgPearlAntiEntity.add(new BoolSetting
			.Builder()
			.id("break-scaffolding")
			.defaultValue(true)
			.visible(this::isPearlMode)
			.build());

	public PhaseConfig(Phase function) {
		super(function);
	}

	private boolean isPearlMode() {
		return mode.get(Phase.Mode.PEARL);
	}
}
