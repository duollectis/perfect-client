package com.perfect.client.core.functional.function.placeFunction;

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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import static com.perfect.client.PerfectClient.clientConfig;

@Getter
public abstract class PlaceFunctionConfig<T extends PlaceFunction> extends FunctionConfig<T> {

	private static final Block[] allowedBlocks = new Block[]{
			Blocks.OBSIDIAN,
			Blocks.BEDROCK,
			Blocks.ENDER_CHEST,
			Blocks.ANVIL,
			Blocks.CHIPPED_ANVIL,
			Blocks.DAMAGED_ANVIL,
			Blocks.REINFORCED_DEEPSLATE,
			Blocks.NETHERITE_BLOCK,
			Blocks.ANCIENT_DEBRIS,
			Blocks.CRYING_OBSIDIAN
	};

	protected final SettingGroup sgGeneral = settings.createGroup();
	protected final SettingGroup sgBreak = settings.createGroup();
	protected final SettingGroup sgRotate = settings.createGroup();
	protected final SettingGroup sgSupport = settings.createGroup();
	protected final SettingGroup sgShift = settings.createGroup();
	protected final SettingGroup sgPause = settings.createGroup();
	protected final SettingGroup sgRender = settings.createGroup();

	private final BlocksSetting blocks = sgGeneral.add(new BlocksSetting
			.Builder()
			.id("blocks")
			.filter(allowedBlocks)
			.defaultValue(Blocks.OBSIDIAN, Blocks.ENDER_CHEST)
			.visible(function::isPlaceEnabled)
			.build());

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(5)
			.range(0, 7)
			.suffix(Suffix.METERS)
			.build());

	private final IntSetting delay = sgGeneral.add(new IntSetting
			.Builder()
			.id("delay")
			.range(0, 2000)
			.sliderRange(0, 500)
			.defaultValue(0)
			.suffix(Suffix.MILLISECONDS)
			.visible(function::isPlaceEnabled)
			.build());

	private final BoolSetting onlyOnGround = sgGeneral.add(new BoolSetting
			.Builder()
			.id("only-on-ground")
			.defaultValue(true)
			.build());

	private final BoolSetting breakCrystals = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-crystals")
			.defaultValue(true)
			.visible(function::isBreakCrystalsFunction)
			.build());

	private final IntSetting breakDelay = sgBreak.add(new IntSetting
			.Builder()
			.id("break-delay")
			.sliderRange(0, 200)
			.range(0, 3000)
			.defaultValue(0)
			.visible(breakCrystals::isVisibleAndActive)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final BoolSetting instantBreak = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-instant")
			.defaultValue(true)
			.visible(() -> breakCrystals.isVisibleAndActive() && function.isInstantProtectFunction())
			.build());

	private final BoolSetting placeRotate = sgRotate.add(new BoolSetting
			.Builder()
			.id("place-rotate")
			.defaultValue(true)
			.build());

	private final BoolSetting breakRotate = sgRotate.add(new BoolSetting
			.Builder()
			.id("break-rotate")
			.defaultValue(true)
			.visible(breakCrystals::isVisibleAndActive)
			.build());

	private final BoolSetting support = sgSupport.add(new BoolSetting
			.Builder()
			.id("support")
			.defaultValue(true)
			.visible(clientConfig::needSupport)
			.build());

	private final IntSetting supportLength = sgSupport.add(new IntSetting
			.Builder()
			.id("support-length")
			.defaultValue(2)
			.range(1, 5)
			.visible(support::isVisibleAndActive)
			.suffix(Suffix.BLOCKS)
			.build());

	private final IntSetting shiftTicks = sgShift.add(new IntSetting
			.Builder()
			.id("shift-ticks")
			.defaultValue(1)
			.visible(function::isPlaceEnabled)
			.build());

	private final IntSetting shiftDelay = sgShift.add(new IntSetting
			.Builder()
			.id("shift-delay")
			.sliderRange(0, 6)
			.defaultValue(0)
			.visible(() -> shiftTicks.get() != 0 && function.isPlaceEnabled())
			.suffix(Suffix.TICKS)
			.build());

	private final BoolSetting pauseOnEat = sgPause.add(new BoolSetting
			.Builder()
			.id("pause-on-eat")
			.defaultValue(false)
			.build());

	private final BoolSetting resourcesToggle = sgPause.add(new BoolSetting
			.Builder()
			.id("resources-toggle")
			.defaultValue(true)
			.build());

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

	public PlaceFunctionConfig(T function) {
		super(function);
	}
}
