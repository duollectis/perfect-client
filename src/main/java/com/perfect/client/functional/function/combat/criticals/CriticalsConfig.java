package com.perfect.client.functional.function.combat.criticals;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import lombok.Getter;

@Getter
public class CriticalsConfig extends FunctionConfig<Criticals> {

    private final SettingGroup sgGeneral = settings.createGroup();
    private final SettingGroup sgPause = settings.createGroup();

    // ————————————————————————————— GENERAL —————————————————————————————— //

    private final EnumSetting<Criticals.Mode> mode = sgGeneral.add(new EnumSetting
            .Builder<>(Criticals.Mode.values())
            .id("mode")
            .defaultValue(Criticals.Mode.PACKET)
            .build());

    private final BoolSetting onlyWithAttackAura = sgGeneral.add(new BoolSetting
            .Builder()
            .id("only-with-attack-aura")
            .defaultValue(false)
            .build());

    private final BoolSetting onlyPhased = sgGeneral.add(new BoolSetting
            .Builder()
            .id("only-phased")
            .defaultValue(false)
            .visible(() -> mode.get(Criticals.Mode.GRIM) || mode.get(Criticals.Mode.GRIM_V3))
            .build());

    private final BoolSetting onlyPhasedWalls = sgGeneral.add(new BoolSetting
            .Builder()
            .id("only-phased-in-walls")
            .defaultValue(false)
            .visible(() -> onlyPhased.get() && (mode.get(Criticals.Mode.GRIM) || mode.get(Criticals.Mode.GRIM_V3)))
            .build());

    // ————————————————————————————————————————————————————————————————————— //

    private final BoolSetting pauseInWeb = sgGeneral.add(new BoolSetting
            .Builder()
            .id("pause-in-web")
            .defaultValue(false)
            .build());

    private final BoolSetting pauseOnMove = sgGeneral.add(new BoolSetting
            .Builder()
            .id("pause-on-move")
            .defaultValue(false)
            .build());

    // ————————————————————————————————————————————————————————————————————— //

    public CriticalsConfig(Criticals function) {
        super(function);
    }
}
