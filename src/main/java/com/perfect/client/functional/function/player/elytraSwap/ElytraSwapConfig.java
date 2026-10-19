package com.perfect.client.functional.function.player.elytraSwap;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class ElytraSwapConfig extends FunctionConfig<ElytraSwap> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final EnumSetting<ElytraSwap.Swap> swap = sgGeneral.add(new EnumSetting
            .Builder<>(ElytraSwap.Swap.values())
            .id("swap")
            .defaultValue(ElytraSwap.Swap.SEMI_ALT)
            .build());

    public ElytraSwapConfig(ElytraSwap function) {
        super(function);
    }
}
