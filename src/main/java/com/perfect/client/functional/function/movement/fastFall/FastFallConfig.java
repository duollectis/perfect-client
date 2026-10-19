package com.perfect.client.functional.function.movement.fastFall;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class FastFallConfig extends FunctionConfig<FastFall> {

	private final SettingGroup sgEarlyFall = settings.createGroup();
	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgSpeed = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();

	private final BoolSetting instantFall = sgEarlyFall.add(new BoolSetting
			.Builder()
			.id("instant-fall")
			.defaultValue(true)
			.build());

	private final BoolSetting onlyHole = sgEarlyFall.add(new BoolSetting
			.Builder()
			.id("only-hole")
			.defaultValue(false)
			.build());

	private final EnumSetting<FastFall.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(FastFall.Mode.values())
			.id("mode")
			.build());

	private final DoubleSetting height = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("height")
			.defaultValue(2)
			.sliderRange(0, 5)
			.visible(this::notNone)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting accelerate = sgSpeed.add(new BoolSetting
			.Builder()
			.id("accelerate")
			.defaultValue(false)
			.visible(this::isMotion)
			.build());

	private final DoubleSetting speed = sgSpeed.add(new DoubleSetting
			.Builder()
			.id("speed")
			.defaultValue(2)
			.sliderRange(0, 5)
			.visible(() -> isMotion() && !accelerate.get())
			.build());

	private final DoubleSetting accelerateSpeed = sgSpeed.add(new DoubleSetting
			.Builder()
			.id("accelerate-speed")
			.defaultValue(0.62)
			.sliderRange(0, 1)
			.visible(() -> isMotion() && accelerate.get())
			.build());

	private final IntSetting packets = sgSpeed.add(new IntSetting
			.Builder()
			.id("shift-ticks")
			.defaultValue(3)
			.range(1, 1000)
			.sliderRange(1, 10)
			.visible(this::isShift)
			.build());

	private final BoolSetting pauseOnLagBack = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-lag-back")
			.defaultValue(false)
			.visible(this::notNone)
			.build());

	private final IntSetting lagBackTime = sgPause.add(new IntSetting
			.Builder()
			.id("pause-time")
			.defaultValue(500)
			.sliderRange(0, 2000)
			.visible(this::notNone)
			.suffix(Suffix.MILLISECONDS)
			.build());

	public FastFallConfig(FastFall function) {
		super(function);
	}

	private boolean isMotion() {
		return mode.is(FastFall.Mode.MOTION);
	}

	private boolean isShift() {
		return mode.is(FastFall.Mode.TICK_SHIFT);
	}

	private boolean notNone() {
		return !mode.is(FastFall.Mode.NONE);
	}
}
