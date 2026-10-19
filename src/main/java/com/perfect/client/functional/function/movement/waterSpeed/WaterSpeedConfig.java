package com.perfect.client.functional.function.movement.waterSpeed;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class WaterSpeedConfig extends FunctionConfig<WaterSpeed> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final DoubleSetting speedLimit = sgGeneral.add(new DoubleSetting
            .Builder()
            .id("speed-limit")
            .defaultValue(20)
            .suffix(Suffix.BPS)
            .build());

    private final DoubleSetting boost = sgGeneral.add(new DoubleSetting
            .Builder()
            .id("boost")
            .range(0.001, 100)
            .sliderRange(1, 10)
            .defaultValue(3)
            .build());

    public WaterSpeedConfig(WaterSpeed function) {
        super(function);
    }
}
