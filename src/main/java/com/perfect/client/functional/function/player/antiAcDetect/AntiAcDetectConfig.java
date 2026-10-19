package com.perfect.client.functional.function.player.antiAcDetect;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class AntiAcDetectConfig extends FunctionConfig<AntiAcDetect> {

	private final SettingGroup sgGeneral = settings.createGroup();

	private final BoolSetting noMultiSwap = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-multi-swap")
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting noAimDuplicate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-aim-duplicate")
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting noSlotDuplicate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-slot-duplicate")
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting noSprintDuplicate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-sprint-duplicate")
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting noInputDuplicate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-input-duplicate")
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting noSelfInteract = sgGeneral.add(new BoolSetting
			.Builder()
			.id("no-self-interact")
			.onChanged(function::onToggled)
			.build());

	public AntiAcDetectConfig(AntiAcDetect function) {
		super(function);
	}
}
