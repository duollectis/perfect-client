package com.perfect.client.functional.function.player.middleClick;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class MiddleClickConfig extends FunctionConfig<MiddleClick> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final EnumSetting<MiddleClick.Mode> mode = sgGeneral.add(new EnumSetting
            .Builder<>(MiddleClick.Mode.values())
            .id("action")
            .defaultValue(MiddleClick.Mode.PEARL)
            .build());

    public MiddleClickConfig(MiddleClick function) {
        super(function);
    }
}
