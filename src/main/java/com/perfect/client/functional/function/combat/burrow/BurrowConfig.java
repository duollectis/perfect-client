package com.perfect.client.functional.function.combat.burrow;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.BlocksSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;
import net.minecraft.block.Blocks;

@Getter
public class BurrowConfig extends FunctionConfig<Burrow> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgClip = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgRenderEffects = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EnumSetting<Burrow.Mode> mode = sgGeneral.add(new EnumSetting
			.Builder<>(Burrow.Mode.values())
			.id("mode")
			.defaultValue(Burrow.Mode.NORMAL)
			.build());

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final BlocksSetting blocks = sgGeneral.add(new BlocksSetting
			.Builder()
			.id("blocks")
			.defaultValue(Blocks.OBSIDIAN, Blocks.ENDER_CHEST)
			.filter(block -> !block.getDefaultState().isReplaceable())
			.build());

	private final BoolSetting disable = sgGeneral.add(new BoolSetting
			.Builder()
			.id("disable")
			.defaultValue(true)
			.build());

	private final IntSetting delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.sliderRange(0, 2000)
			.defaultValue(500)
			.suffix(Suffix.MILLISECONDS)
			.build());

	public final BoolSetting rotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("rotate")
			.defaultValue(true)
			.build());

	// —————————————————————————————— CLIP ———————————————————————————————— //

	private final DoubleSetting vClip = sgClip.add(new DoubleSetting
			.Builder()
			.id("v-clip")
			.defaultValue(3)
			.range(-10000, 10000)
			.suffix(Suffix.METERS)
			.visible(this::isNormalMode)
			.build());

	// ————————————————————————————— RENDER ——————————————————————————————— //

	private final BoolSetting render = sgRender.add(new BoolSetting
			.Builder()
			.id("render")
			.defaultValue(true)
			.build());

	private final BundleSetting<AnimatedCosmeticsBundle> cosmetics = sgRender.add(new BundleSetting
			.Builder<AnimatedCosmeticsBundle>()
			.id("cosmetics")
			.defaultValue(new AnimatedCosmeticsBundle())
			.build());

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

	public BurrowConfig(Burrow function) {
		super(function);
	}

	private boolean isNormalMode() {
		return mode.get(Burrow.Mode.NORMAL);
	}
}
