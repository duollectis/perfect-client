package com.perfect.client.functional.function.visuals.aspectRatio;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import lombok.Getter;

@Getter
public class AspectRatioConfig extends FunctionConfig<AspectRatio> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final EnumSetting<AspectRatio.Mode> mode = sgGeneral.add(new EnumSetting
            .Builder<>(AspectRatio.Mode.values())
            .id("ratio")
            .defaultValue(AspectRatio.Mode._16_10)
            .build());

    private final DoubleSetting value = sgGeneral.add(new DoubleSetting
            .Builder()
            .id("custom-value")
            .defaultValue(1.77)
            .min(0.2)
            .sliderRange(0.5, 3)
            .visible(() -> mode.get() == AspectRatio.Mode.CUSTOM)
            .build());

    public AspectRatioConfig(AspectRatio function) {
        super(function);
    }
}
