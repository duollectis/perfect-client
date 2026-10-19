package com.perfect.client.functional.function.combat.anchorAura;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.BlocksSetting;
import com.perfect.client.core.functional.setting.impl.list.EntitiesSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;
import net.minecraft.block.Blocks;

@Getter
public class AnchorAuraConfig extends FunctionConfig<AnchorAura> {

    private final SettingGroup sgGeneral = settings.createGroup();
    private final SettingGroup sgAntiProtect = settings.createGroup();
    private final SettingGroup sgPlace = settings.createGroup();
    private final SettingGroup sgDamages = settings.createGroup();
    private final SettingGroup sgPause = settings.createGroup();
    private final SettingGroup sgRender = settings.createGroup();
    private final SettingGroup sgRenderEffects = settings.createGroup();

    // ————————————————————————————— GENERAL ——————————————————————————————— //

    private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
            .Builder<>(Swap.values())
            .id("swap")
            .defaultValue(Swap.HOTBAR)
            .build());

    private final EntitiesSetting targets = sgGeneral.add(new EntitiesSetting
            .Builder()
            .id("targets")
            .defaultGroups(EntityGroup.PLAYERS)
            .onlyAttackable()
            .build());

    private final DoubleSetting targetRange = sgGeneral.add(new DoubleSetting
            .Builder()
            .id("target-range")
            .defaultValue(10)
            .range(1, 20)
            .suffix(Suffix.METERS)
            .build());

    private final BoolSetting ignoreTerrain = sgGeneral.add(new BoolSetting
            .Builder()
            .id("ignore-terrain")
            .defaultValue(true)
            .build());

    private final BoolSetting breakTrap = sgGeneral.add(new BoolSetting
            .Builder()
            .id("break-trap")
            .defaultValue(true)
            .build());

    private final BoolSetting rotate = sgGeneral.add(new BoolSetting
            .Builder()
            .id("rotate")
            .defaultValue(false)
            .build());

    // —————————————————————————— ANTI PROTECT ———————————————————————————— //

    private final BoolSetting antiProtect = sgAntiProtect.add(new BoolSetting
            .Builder()
            .id("anti-protect")
            .defaultValue(false)
            .build());

    private final BlocksSetting antiProtectBlock = sgAntiProtect.add(new BlocksSetting
            .Builder()
            .id("replacement-block")
            .filter(b -> !b.getDefaultState().isReplaceable() && b.getHardness() < 25)
            .defaultValue(Blocks.ENDER_CHEST, Blocks.GLOWSTONE)
            .build());

    // —————————————————————————————— PLACE ———————————————————————————————— //

    private final EnumSetting<AnchorAura.Sequential> sequential = sgPlace.add(new EnumSetting
            .Builder<>(AnchorAura.Sequential.values())
            .id("sequential")
            .defaultValue(AnchorAura.Sequential.NONE)
            .build());

    private final IntSetting placeDelay = sgPlace.add(new IntSetting
            .Builder()
            .id("place-delay")
            .defaultValue(0)
            .sliderRange(0, 10)
            .suffix(Suffix.TICKS)
            .build());

    private final IntSetting breakDelay = sgPlace.add(new IntSetting
            .Builder()
            .id("break-delay")
            .defaultValue(0)
            .sliderRange(0, 10)
            .suffix(Suffix.TICKS)
            .build());

    private final DoubleSetting interactRange = sgPlace.add(new DoubleSetting.Builder()
            .id("interact-range")
            .defaultValue(5)
            .range(0, 7)
            .suffix(Suffix.METERS)
            .build());

    // ———————————————————————————— DAMAGES —————————————————————————————— //

    private final DoubleSetting minDamage = sgDamages.add(new DoubleSetting
            .Builder()
            .id("min-damage")
            .defaultValue(10)
            .range(0, 10000)
            .sliderRange(0, 36)
            .build());

    private final DoubleSetting maxSelfDamage = sgDamages.add(new DoubleSetting
            .Builder()
            .id("max-self-damage")
            .defaultValue(5)
            .range(0, 10000)
            .sliderRange(0, 36)
            .build());

    private final BoolSetting antiSelfPop = sgDamages.add(new BoolSetting
            .Builder()
            .id("anti-self-pop")
            .defaultValue(true)
            .build());

    private final BoolSetting antiFriendPop = sgDamages.add(new BoolSetting
            .Builder()
            .id("anti-friend-pop")
            .defaultValue(true)
            .build());

    // —————————————————————————————— PAUSE ———————————————————————————————— //

    private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
            .Builder()
            .id("pause-on-eat")
            .defaultValue(true)
            .build());

    private final BoolSetting pauseOnBlink = sgPause.add(new BoolSetting
            .Builder()
            .id("pause-on-blink")
            .defaultValue(true)
            .build());

    // ————————————————————————————— RENDER ———————————————————————————————— //

    private final BoolSetting render = sgRender.add(new BoolSetting
            .Builder()
            .id("render")
            .defaultValue(true)
            .build());

    private final BundleSetting<AnimatedCosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
            .Builder<AnimatedCosmeticsBundle>()
            .id("cosmetics")
            .defaultValue(new AnimatedCosmeticsBundle())
            .build()
    );

    private final MultiColorSetting lineColor = sgRender.add(new MultiColorSetting
            .Builder()
            .id("line-color")
            .defaultValue(MultiColor.client())
            .visible(render::get)
            .build());

    private final MultiColorSetting sideColor = sgRender.add(new MultiColorSetting
            .Builder()
            .id("side-color")
            .defaultValue(MultiColor.clientFill())
            .visible(render::get)
            .build());

    // ————————————————————————————————————————————————————————————————————— //

    public AnchorAuraConfig(AnchorAura function) {
        super(function);
    }
}
