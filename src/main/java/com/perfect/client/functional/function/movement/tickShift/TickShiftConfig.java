package com.perfect.client.functional.function.movement.tickShift;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class TickShiftConfig extends FunctionConfig<TickShift> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgTickShift = settings.createGroup();
	private final SettingGroup sgBlink = settings.createGroup();

	private final BoolSetting infinite = sgGeneral.add(new BoolSetting
			.Builder()
			.id("infinite")
			.defaultValue(false)
			.build());

	private final BoolSetting legit = sgGeneral.add(new BoolSetting
			.Builder()
			.id("legit")
			.defaultValue(false)
			.visible(() -> !infinite.get())
			.build());

	private final BoolSetting keepPackets = sgGeneral.add(new BoolSetting
			.Builder()
			.id("keep-packets")
			.defaultValue(false)
			.visible(() -> !infinite.get())
			.build());

	private final IntSetting packetsInTick = sgTickShift.add(new IntSetting
			.Builder()
			.id("packets-in-tick")
			.defaultValue(3)
			.range(0, 10000)
			.build());

	private final IntSetting maxPackets = sgTickShift.add(new IntSetting
			.Builder()
			.id("max-packets")
			.defaultValue(15)
			.range(1, 10000)
			.sliderRange(0, 30)
			.visible(() -> !infinite.get())
			.build());

	private final DoubleSetting chargeSpeed = sgTickShift.add(new DoubleSetting
			.Builder()
			.id("charge-speed")
			.defaultValue(1)
			.sliderRange(0.5, 3)
			.range(0, 10000)
			.suffix(Suffix.TICKS)
			.visible(() -> !infinite.get())
			.build());

	private final DoubleSetting minCharge = sgTickShift.add(new DoubleSetting
			.Builder()
			.id("min-charge")
			.defaultValue(2)
			.range(0, 100)
			.sliderMax(100)
			.suffix(Suffix.PERCENT)
			.visible(() -> !infinite.get())
			.build());

	private final BoolSetting useBlink = sgBlink.add(new BoolSetting
			.Builder()
			.id("use-blink")
			.defaultValue(false)
			.visible(() -> !infinite.get())
			.build());

	private final IntSetting blinkTicks = sgBlink.add(new IntSetting
			.Builder()
			.id("blink-ticks")
			.defaultValue(3)
			.sliderRange(1, 5)
			.range(1, 50)
			.visible(useBlink::get)
			.visible(() -> !infinite.get())
			.build());

	public TickShiftConfig(TickShift function) {
		super(function);
	}
}
