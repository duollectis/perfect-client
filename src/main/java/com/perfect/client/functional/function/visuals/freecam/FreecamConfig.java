package com.perfect.client.functional.function.visuals.freecam;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class FreecamConfig extends FunctionConfig<Freecam> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAdditional = settings.createGroup();

	private final DoubleSetting speed = sgGeneral.add(new DoubleSetting.Builder()
			.id("speed")
			.defaultValue(50.0)
			.min(0.0)
			.sliderRange(10.0, 80.0)
			.suffix(Suffix.BPS)
			.build());

	private final DoubleSetting smoothness = sgGeneral.add(new DoubleSetting.Builder()
			.id("smoothness")
			.range(0.0, 100.0)
			.sliderMax(100.0)
			.defaultValue(90.0)
			.suffix(Suffix.PERCENT)
			.build());

	private final BoolSetting interactions = sgGeneral.add(new BoolSetting
			.Builder()
			.id("interactions")
			.defaultValue(false)
			.build());

	private final BoolSetting rotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(false)
			.visible(interactions::get)
			.build());

	private final BoolSetting noHandRender = sgAdditional.add(new BoolSetting
			.Builder()
			.id("no-hand-render")
			.defaultValue(false)
			.build());

	private final BindSetting changeControlBind = sgAdditional.add(new BindSetting
			.Builder()
			.id("change-control-bind")
			.action(function::changeControl)
			.build());

	public FreecamConfig(Freecam function) {
		super(function);
	}
}
