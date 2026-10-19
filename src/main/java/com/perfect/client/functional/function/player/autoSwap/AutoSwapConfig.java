package com.perfect.client.functional.function.player.autoSwap;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import lombok.Getter;

@Getter
public class AutoSwapConfig extends FunctionConfig<AutoSwap> {

    private final SettingGroup sgGeneral = settings.createGroup();
    private final SettingGroup sgBypass = settings.createGroup();
    private final SettingGroup sgBinds = settings.createGroup();

    private final BoolSetting inGui = sgGeneral.add(new BoolSetting
            .Builder()
            .id("in-gui")
            .defaultValue(true)
            .build());

    private final BoolSetting fromInventory = sgGeneral.add(new BoolSetting
            .Builder()
            .id("inv-swap")
            .defaultValue(true)
            .build());

    private final BoolSetting bypassSprint = sgBypass.add(new BoolSetting
            .Builder()
            .id("legit-sprint")
            .defaultValue(false)
            .build());

    private final BoolSetting bypassMovement = sgBypass.add(new BoolSetting
            .Builder()
            .id("legit-movement")
            .defaultValue(false)
            .build());

    private final BoolSetting bypassTime = sgBypass.add(new BoolSetting
            .Builder()
            .id("legit-time")
            .defaultValue(false)
            .build());

    private final IntSetting ticksPre = sgBypass.add(new IntSetting
            .Builder()
            .id("pre-ticks")
            .sliderRange(0, 5)
            .range(0, 10)
            .build());

    private final IntSetting ticksPost = sgBypass.add(new IntSetting
            .Builder()
            .id("post-ticks")
            .sliderRange(0, 5)
            .range(0, 10)
            .build());

    private final BindSetting pearlBind = sgBinds.add(new BindSetting
            .Builder()
            .id("pearl")
            .action(() -> function.setItem(AutoSwap.SwapItem.PEARL))
            .build());

    private final BindSetting trapBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-trap")
            .action(() -> function.setItem(AutoSwap.SwapItem.TRAP))
            .build());

    private final BindSetting layerBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-layer")
            .action(() -> function.setItem(AutoSwap.SwapItem.LAYER))
            .build());

    private final BindSetting disorientBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-disorientation")
            .action(() -> function.setItem(AutoSwap.SwapItem.DISORIENT))
            .build());

    private final BindSetting freezingBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-freezing")
            .action(() -> function.setItem(AutoSwap.SwapItem.FREEZING_BALL))
            .build());

    private final BindSetting godsAuraBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-god-aura")
            .action(() -> function.setItem(AutoSwap.SwapItem.GODS_AURA))
            .build());

    private final BindSetting visibleDustBind = sgBinds.add(new BindSetting
            .Builder()
            .id("ft-visible-dust")
            .action(() -> function.setItem(AutoSwap.SwapItem.VISIBLE_DUST))
            .build());

    public AutoSwapConfig(AutoSwap function) {
        super(function);
    }
}
