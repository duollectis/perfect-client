package com.perfect.client.functional.function.player.autoTool;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.functions.inventory.Swap;
import com.perfect.client.core.utils.functions.mining.ToolUtils;
import com.perfect.client.core.utils.text.Suffix;
import lombok.Getter;

@Getter
public class AutoToolConfig extends FunctionConfig<AutoTool> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgPreference = settings.createGroup();

	private final EnumSetting<Swap> swap = sgGeneral.add(new EnumSetting
			.Builder<>(Swap.values())
			.id("swap")
			.defaultValue(Swap.HOTBAR)
			.build());

	private final IntRangeSetting swapDelay = sgGeneral.add(new IntRangeSetting
			.Builder()
			.id("swap-delay")
			.suffix(Suffix.TICKS)
			.range(0, 20)
			.sliderRange(0, 3)
			.defaultValue(1, 1)
			.onChanged(function::getSwapDelay)
			.build());

	private final EnumSetting<ToolUtils.Enchantment> prefer = sgPreference.add(new EnumSetting
			.Builder<>(ToolUtils.Enchantment.values())
			.id("enchantment")
			.defaultValue(ToolUtils.Enchantment.FORTUNE)
			.build());

	private final BoolSetting enderChestSilkTouch = sgPreference.add(new BoolSetting
			.Builder()
			.id("ec-silk-touch")
			.defaultValue(true)
			.build());

	public AutoToolConfig(AutoTool function) {
		super(function);
	}
}
