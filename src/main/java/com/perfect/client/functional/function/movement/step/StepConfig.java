package com.perfect.client.functional.function.movement.step;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class StepConfig extends FunctionConfig<Step> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgMovement = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();

	private final EnumSetting<Step.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(Step.Mode.values())
			.id("mode")
			.defaultValue(Step.Mode.NORMAL)
			.onChangedMode(function::getModeContainer)
			.build());

	private final BoolSetting onlyOnGround = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-on-ground")
			.defaultValue(true)
			.build());

	private final DoubleSetting height = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("height")
			.defaultValue(2)
			.sliderRange(1, 3)
			.range(0, 1000)
			.suffix(Suffix.METERS)
			.visible(() -> !isSlowMode())
			.build());

	private final DoubleSetting normalHeight = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("normal-height")
			.defaultValue(1)
			.sliderRange(0, 3)
			.range(0, 1000)
			.suffix(Suffix.METERS)
			.visible(this::isSlowMode)
			.build());

	private final DoubleSetting slowHeight = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("slow-height")
			.defaultValue(2)
			.sliderRange(0, 3)
			.range(0, 1000)
			.suffix(Suffix.METERS)
			.visible(this::isSlowMode)
			.build());

	private final DoubleSetting speed = sgMovement.add(new DoubleSetting
			.Builder()
			.id("speed")
			.defaultValue(1)
			.sliderRange(0, 5)
			.suffix(Suffix.BPS)
			.visible(this::isSlowMode)
			.build());

	private final DoubleSetting heightSpeedFactor = sgMovement.add(new DoubleSetting
			.Builder()
			.id("height-speed-factor")
			.defaultValue(0)
			.sliderRange(0, 3)
			.visible(this::isSlowMode)
			.build());

	private final DoubleSetting jumpSpeed = sgMovement.add(new DoubleSetting
			.Builder()
			.id("jump-speed")
			.defaultValue(0)
			.sliderRange(0, 8)
			.suffix(Suffix.BPS)
			.visible(this::isSlowMode)
			.build());

	private final BoolSetting pauseOnShift = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-shift")
			.defaultValue(false)
			.build());

	private final BoolSetting pauseOnLagBack = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-lag-back")
			.defaultValue(false)
			.build());

	private final IntSetting pauseTime = sgPause.add(new IntSetting
			.Builder()
			.id("pause-time")
			.defaultValue(200)
			.sliderRange(0, 2000)
			.suffix(Suffix.MILLISECONDS)
			.visible(pauseOnLagBack::get)
			.build());

	public StepConfig(Step function) {
		super(function);
	}

	private boolean isSlowMode() {
		return mode.get(Step.Mode.SLOW);
	}
}
