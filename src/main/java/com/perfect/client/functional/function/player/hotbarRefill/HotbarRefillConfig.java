package com.perfect.client.functional.function.player.hotbarRefill;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import lombok.Getter;

@Getter
public class HotbarRefillConfig extends FunctionConfig<HotbarRefill> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final IntSetting count = sgGeneral.add(new IntSetting
            .Builder()
            .id("count")
            .defaultValue(4)
            .min(1)
            .sliderRange(1, 63)
            .build());

    private final BoolSetting singleItems = sgGeneral.add(new BoolSetting
            .Builder()
            .id("single-items")
            .defaultValue(true)
            .visible(() -> false)
            .build());

    private final DoubleSetting delay = sgGeneral.add(new DoubleSetting
            .Builder()
            .id("delay")
            .range(0, 1)
            .defaultValue(0)
            .build());

    private final BoolSetting offhand = sgGeneral.add(new BoolSetting
            .Builder()
            .id("offhand")
            .defaultValue(true)
            .build());

    public HotbarRefillConfig(HotbarRefill function) {
        super(function);
    }
}
