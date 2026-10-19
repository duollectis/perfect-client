package com.perfect.client.functional.function.combat.surround;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class SurroundConfig extends PlaceFunctionConfig<Surround> {

    private final BoolSetting yToggle = sgPause.add(new BoolSetting
            .Builder()
            .id("toggle-on-jump")
            .defaultValue(true)
            .build());

    private final BoolSetting toggleOnChorus = sgPause.add(new BoolSetting
            .Builder()
            .id("toggle-on-chorus")
            .defaultValue(true)
            .build());

    private final BoolSetting pauseStep = sgPause.add(new BoolSetting
            .Builder()
            .id("pause-step")
            .defaultValue(false)
            .build());

    public SurroundConfig(Surround function) {
        super(function);
    }
}
