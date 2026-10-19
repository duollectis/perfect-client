package com.perfect.client.functional.function.player.rotation;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import lombok.Getter;

@Getter
public class RotationConfig extends FunctionConfig<Rotation> {

    private final SettingGroup sgGeneral = settings.createGroup();
    private final SettingGroup sgYaw = settings.createGroup();
    private final SettingGroup sgPitch = settings.createGroup();

    private final BoolSetting clientLook = sgGeneral.add(new BoolSetting
            .Builder()
            .id("client-look")
            .defaultValue(false)
            .build());

    private final EnumSetting<Rotation.Mode> yawMode = sgYaw.add(new EnumSetting
            .Builder<>(Rotation.Mode.values())
            .id("yaw")
            .defaultValue(Rotation.Mode.SMART)
            .build());

    private final DoubleSetting yawValue = sgYaw.add(new DoubleSetting.
            Builder()
            .id("yaw-value")
            .range(-180, 180)
            .sliderRange(-180, 180)
            .defaultValue(0)
            .visible(() -> yawMode.get() == Rotation.Mode.CUSTOM)
            .build());

    private final EnumSetting<Rotation.Mode> pitchMode = sgPitch.add(new EnumSetting
            .Builder<>(Rotation.Mode.values())
            .id("pitch")
            .defaultValue(Rotation.Mode.SMART)
            .build());

    private final DoubleSetting pitchValue = sgPitch.add(new DoubleSetting
            .Builder()
            .id("pitch-value")
            .range(-90, 90)
            .sliderRange(-90, 90)
            .defaultValue(0)
            .visible(() -> pitchMode.get() == Rotation.Mode.CUSTOM)
            .build());

    public RotationConfig(Rotation function) {
        super(function);
    }
}
