package com.perfect.client.functional.function.player.noServerRotate;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import lombok.Getter;

@Getter
public class NoServerRotateConfig extends FunctionConfig<NoServerRotate> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final BoolSetting onElytra = sgGeneral.add(new BoolSetting
            .Builder()
            .id("on-elytra")
            .defaultValue(false)
            .build());

    private final BoolSetting strict = sgGeneral.add(new BoolSetting
            .Builder()
            .id("strict")
            .defaultValue(false)
            .onChanged(i -> function.reset())
            .build());

    public NoServerRotateConfig(NoServerRotate function) {
        super(function);
    }
}
