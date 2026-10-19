package com.perfect.client.functional.function.player.noDelay;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class NoDelayConfig extends FunctionConfig<NoDelay> {

    private final SettingGroup sgGeneral = settings.createGroup();

    private final EnumListSetting<NoDelay.Actions> actions = sgGeneral.add(new EnumListSetting
            .Builder<>(NoDelay.Actions.values())
            .id("actions")
            .build());

    private final IntRangeSetting jumpDelay = sgGeneral.add(new IntRangeSetting
            .Builder()
            .id("jump-delay")
            .defaultValue(0, 0)
            .sliderRange(0, 5)
            .suffix(Suffix.TICKS)
            .visible(() -> actions.get(NoDelay.Actions.JUMP))
            .onChanged(function::getJumpDelay)
            .build());

    private final IntRangeSetting placeDelay = sgGeneral.add(new IntRangeSetting
            .Builder()
            .id("place-delay")
            .defaultValue(0, 0)
            .sliderRange(0, 5)
            .suffix(Suffix.TICKS)
            .visible(() -> actions.get(NoDelay.Actions.PLACE))
            .onChanged(function::getPlaceDelay)
            .build());

    private final IntRangeSetting breakDelay = sgGeneral.add(new IntRangeSetting
            .Builder()
            .id("break-delay")
            .defaultValue(0, 0)
            .sliderRange(0, 5)
            .suffix(Suffix.TICKS)
            .visible(() -> actions.get(NoDelay.Actions.BREAK))
            .onChanged(function::getBreakDelay)
            .build());

    private final IntRangeSetting expDelay = sgGeneral.add(new IntRangeSetting
            .Builder()
            .id("xp-delay")
            .defaultValue(0, 0)
            .sliderRange(0, 5)
            .suffix(Suffix.TICKS)
            .visible(() -> actions.get(NoDelay.Actions.EXP))
            .onChanged(function::getExpDelay)
            .build());

    public NoDelayConfig(NoDelay function) {
        super(function);
    }
}
