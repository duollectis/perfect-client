package com.perfect.client.functional.function.combat.holeFill;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class HoleFillConfig extends PlaceFunctionConfig<HoleFill> {

    private final SettingGroup sgRange = settings.createGroup(0);
    private final SettingGroup sgPlane = settings.createGroup(1);
    private final SettingGroup sgTarget = settings.createGroup(2);
    private final SettingGroup sgHole = settings.createGroup(3);

    private final BoolSetting withoutTarget = sgPlane.add(new BoolSetting
            .Builder()
            .id("without-target")
            .defaultValue(true)
            .build());

    private final DoubleSetting minRange = sgRange.add(new DoubleSetting
            .Builder()
            .id("min-range")
            .defaultValue(0.5)
            .sliderRange(0, 6)
            .range(0, 10)
            .suffix(Suffix.METERS)
            .visible(() -> !withoutTarget.get())
            .build());

    private final DoubleSetting radius = sgRange.add(new DoubleSetting
            .Builder()
            .id("range-from-target")
            .defaultValue(3)
            .sliderRange(0, 6)
            .range(0, 10)
            .suffix(Suffix.METERS)
            .visible(() -> !withoutTarget.get())
            .build());

    private final DoubleSetting yRange = sgRange.add(new DoubleSetting
            .Builder()
            .id("y-range")
            .defaultValue(3)
            .sliderRange(0, 6)
            .range(0, 10)
            .suffix(Suffix.METERS)
            .visible(() -> !withoutTarget.get())
            .build());

    private final BoolSetting onlyInHole = sgTarget.add(new BoolSetting
            .Builder()
            .id("only-in-hole")
            .defaultValue(true)
            .visible(() -> !withoutTarget.get())
            .build());

    private final BoolSetting ignoreRangeInHole = sgTarget.add(new BoolSetting
            .Builder()
            .id("safe-in-hole")
            .defaultValue(true)
            .visible(() -> !withoutTarget.get())
            .build());

    private final IntSetting extrapolation = sgTarget.add(new IntSetting
            .Builder()
            .id("extrapolation")
            .defaultValue(3)
            .sliderRange(0, 10)
            .range(0, 30)
            .suffix(Suffix.TICKS)
            .visible(() -> !withoutTarget.get())
            .build());

    private final IntSetting height = sgHole.add(new IntSetting
            .Builder()
            .id("hole-height")
            .defaultValue(3)
            .suffix(Suffix.BLOCKS)
            .build());

    private final BoolSetting singleHoles = sgHole.add(new BoolSetting
            .Builder()
            .id("single-holes")
            .defaultValue(true)
            .build());

    private final BoolSetting doubleHoles = sgHole.add(new BoolSetting
            .Builder()
            .id("double-holes")
            .defaultValue(true)
            .build());

    private final BoolSetting quadHoles = sgHole.add(new BoolSetting
            .Builder()
            .id("quad-holes")
            .defaultValue(true)
            .build());

    public HoleFillConfig(HoleFill function) {
        super(function);
    }
}
