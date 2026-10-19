package com.perfect.client.functional.function.combat.autoMine;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.bundles.AnimatedCosmeticsBundle;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.BundleSetting;
import com.perfect.client.core.functional.setting.impl.color.MultiColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumOrderSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.text.Suffix;
import com.perfect.client.functional.function.combat.autoMine.helpers.AutoMineType;
import lombok.Getter;

import static com.perfect.client.PerfectClient.clientConfig;

@Getter
public class AutoMineConfig extends FunctionConfig<AutoMine> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgRebreak = settings.createGroup();
	private final SettingGroup sgPlace = settings.createGroup();
	private final SettingGroup sgBreak = settings.createGroup();
	private final SettingGroup sgPositions = settings.createGroup();
	private final SettingGroup sgCev = settings.createGroup();
	private final SettingGroup sgPause = settings.createGroup();
	private final SettingGroup sgRender = settings.createGroup();
	private final SettingGroup sgRenderEffects = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.ALTERNATIVE)
			.build());

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.range(1, 7)
			.defaultValue(5)
			.suffix(Suffix.METERS).build());

	private final DoubleSetting maxSelfDamage = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("max-self-damage")
			.range(0, 36)
			.defaultValue(4)
			.build());

	private final BoolSetting instantRotate = sgGeneral.add(new BoolSetting
			.Builder()
			.id("instant-rotate")
			.defaultValue(true)
			.build());

	// ————————————————————————————— REBREAK ——————————————————————————————— //

	private final BoolSetting waitRebreak = sgRebreak.add(new BoolSetting
			.Builder()
			.id("wait-re-break")
			.defaultValue(false)
			.build());

	private final DoubleSetting rebreakProgress = sgRebreak.add(new DoubleSetting
			.Builder()
			.id("re-break-progress")
			.range(0, 100)
			.sliderMax(100)
			.defaultValue(100)
			.visible(waitRebreak::get)
			.suffix(Suffix.PERCENT)
			.build());

	private final BoolSetting preBreak = sgRebreak.add(new BoolSetting
			.Builder()
			.id("air-pre-break")
			.defaultValue(true)
			.visible(waitRebreak::get)
			.build());

	// ————————————————————————————— PLACE ——————————————————————————————— //

	private final BoolSetting placeCrystal = sgPlace.add(new BoolSetting
			.Builder()
			.id("place-crystal")
			.defaultValue(true)
			.build());

	private final BoolSetting crystalSupport = sgPlace.add(new BoolSetting
			.Builder()
			.id("crystal-support")
			.defaultValue(true)
			.visible(placeCrystal::get)
			.build());

	private final BoolSetting support = sgPlace.add(new BoolSetting
			.Builder()
			.id("place-support")
			.defaultValue(true)
			.visible(() -> crystalSupport.get() && crystalSupport.isVisible() && clientConfig.needSupport())
			.build());

	private final IntSetting supportLength = sgPlace.add(new IntSetting
			.Builder()
			.id("support-length")
			.defaultValue(2)
			.range(1, 5)
			.suffix(Suffix.BLOCKS)
			.visible(() -> support.isVisible() && support.get())
			.build());

	private final BoolSetting supportOnlyInHole = sgPlace.add(new BoolSetting
			.Builder()
			.id("support-only-in-hole")
			.defaultValue(true)
			.visible(crystalSupport::get)
			.build());

	// ————————————————————————————— BREAK ——————————————————————————————— //

	private final BoolSetting breakObstructive = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-obstructive")
			.defaultValue(true)
			.build());

	private final BoolSetting breakCrystal = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-crystal")
			.defaultValue(true)
			.build());

	private final BoolSetting breakOnlyTrapCev = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-only-trap-cev")
			.defaultValue(true)
			.visible(breakCrystal::get)
			.build());

	// ———————————————————————————— POSITIONS ————————————————————————————— //

	private final EnumOrderSetting<AutoMineType> positions = sgPositions.add(new EnumOrderSetting
			.Builder<>(AutoMineType.values)
			.id("types")
			.build()
	);

	// ——————————————————————————————— CEV ——————————————————————————————— //

	private final DoubleSetting minCevDamage = sgCev.add(new DoubleSetting
			.Builder()
			.id("min-cev-damage")
			.range(0d, 36)
			.defaultValue(5)
			.visible(this::isCev)
			.build());

	// ————————————————————————————— PAUSE ——————————————————————————————— //

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

	// ———————————————————————————— RENDER ——————————————————————————————— //

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

	public AutoMineConfig(AutoMine function) {
		super(function);
	}

	public boolean isCev() {
		return positions.isEnabled(AutoMineType.TRAP_CEV) || positions.isEnabled(AutoMineType.FAR_CEV);
	}
}
