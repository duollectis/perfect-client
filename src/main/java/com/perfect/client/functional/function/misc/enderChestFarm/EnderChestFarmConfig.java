package com.perfect.client.functional.function.misc.enderChestFarm;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.utils.functions.interact.Rotate;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

import static com.perfect.client.PerfectClient.clientConfig;

@Getter
public class EnderChestFarmConfig extends FunctionConfig<EnderChestFarm> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgProtect = settings.createGroup();
	private final SettingGroup sgPlace = settings.createGroup();
	private final SettingGroup sgBreak = settings.createGroup();

	// ————————————————————————————— GENERAL ——————————————————————————————— //

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final DoubleSetting range = sgGeneral.add(new DoubleSetting
			.Builder()
			.id("range")
			.defaultValue(5)
			.range(0, 6)
			.suffix(Suffix.METERS)
			.build());

	private final BoolSetting mine = sgGeneral.add(new BoolSetting
			.Builder()
			.id("mine")
			.defaultValue(true)
			.build());

	// ————————————————————————————— PROTECT ——————————————————————————————— //

	private final BoolSetting sideProtect = sgProtect.add(new BoolSetting
			.Builder()
			.id("side-protect")
			.defaultValue(false)
			.build());

	private final BoolSetting topProtect = sgProtect.add(new BoolSetting
			.Builder()
			.id("top-protect")
			.defaultValue(false)
			.build());

	// ————————————————————————————— PLACE ——————————————————————————————— //

	private final EnumSetting<Rotate> rotate = sgPlace.add(new EnumSetting
			.Builder<>(Rotate.values())
			.id("rotate")
			.defaultValue(Rotate.VANILLA)
			.build());

	private final BoolSetting support = sgPlace.add(new BoolSetting
			.Builder()
			.id("support")
			.defaultValue(false)
			.visible(() -> clientConfig.needSupport() && (sideProtect.get() || topProtect.get()))
			.build());

	private final IntSetting supportLength = sgPlace.add(new IntSetting
			.Builder()
			.id("support-length")
			.defaultValue(2)
			.range(1, 5)
			.suffix(Suffix.BLOCKS)
			.visible(() -> support.isVisible() && support.get())
			.build());

	// —————————————————————————————— BREAK ———————————————————————————————— //

	private final BoolSetting breakCrystals = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-crystals")
			.defaultValue(true)
			.build());

	private final BoolSetting breakRotate = sgBreak.add(new BoolSetting
			.Builder()
			.id("break-rotate")
			.defaultValue(false)
			.visible(breakCrystals::get)
			.build());

	// ————————————————————————————————————————————————————————————————————— //

	public EnderChestFarmConfig(EnderChestFarm function) {
		super(function);
	}
}
