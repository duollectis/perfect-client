package com.perfect.client.functional.function.combat.selfTrap;

import com.perfect.client.core.functional.function.placeFunction.PlaceFunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.utils.functions.Trap;
import lombok.Getter;

@Getter
public class SelfTrapConfig extends PlaceFunctionConfig<SelfTrap> {

    private final SettingGroup sgSelfTrap = settings.createGroup(0);

    private final EnumSetting<Trap> mode = sgSelfTrap.add(new EnumSetting
            .Builder<>(Trap.values())
            .id("mode")
            .defaultValue(Trap.HEAD)
            .build());

    private final BoolSetting antiCev = sgSelfTrap.add(new BoolSetting
            .Builder()
            .id("anti-cev")
            .defaultValue(true)
            .build());

    private final BoolSetting toggleOnChorus = sgPause.add(new BoolSetting
            .Builder()
            .id("toggle-on-chorus")
            .defaultValue(true)
            .build());

    public SelfTrapConfig(SelfTrap function) {
        super(function);
    }
}
