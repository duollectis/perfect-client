package com.perfect.client.functional.function.movement.holeSnap;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class HoleSnapConfig extends FunctionConfig<HoleSnap> {

	private final SettingGroup sgHole = settings.createGroup();
	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgMotion = settings.createGroup();

	private final EnumListSetting<HoleSnap.HoleTypes> holeTypes = sgHole.add(new EnumListSetting
			.Builder<>(HoleSnap.HoleTypes.values())
			.id("hole-types")
			.build());

	private final BoolSetting onlyVisibleHoles = sgHole.add(new BoolSetting
			.Builder()
			.id("only-visible-holes")
			.defaultValue(false)
			.build());

	private final BoolSetting rotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(false)
			.build());

	private final BoolSetting tickShift = sgGeneral.add(new BoolSetting
			.Builder()
			.id("tick-shift")
			.defaultValue(false)
			.build());

	private final DoubleSetting rangeXZ = sgMotion.add(new DoubleSetting
			.Builder()
			.id("range-xz")
			.defaultValue(3)
			.range(1, 50)
			.sliderRange(1, 5)
			.suffix(Suffix.METERS)
			.build());

	private final DoubleSetting rangeY = sgMotion.add(new DoubleSetting
			.Builder()
			.id("range-y")
			.defaultValue(3)
			.range(1, 50)
			.sliderRange(1, 5)
			.suffix(Suffix.METERS)
			.build());

	public HoleSnapConfig(HoleSnap function) {
		super(function);
	}
}
