package com.perfect.client.functional.function.player.autoBuy;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.list.ItemPriceSetting;
import com.perfect.client.core.functional.setting.impl.number.DoubleSetting;
import com.perfect.client.core.functional.setting.impl.number.IntListSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.core.utils.text.Suffix;
import com.perfect.client.manager.game.item.server.FunTimeItems;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

@Getter
public class AutoBuyConfig extends FunctionConfig<AutoBuy> {

	private static final Item[] funTimeItems = new Item[]{
			Items.GUNPOWDER,
			Items.SHULKER_SPAWN_EGG,
			Items.HEAVY_CORE,
			Items.GOLD_INGOT,
			Items.DIAMOND,
			Items.NETHERITE_INGOT,
			Items.ANCIENT_DEBRIS,
			Items.GOLD_BLOCK,
			Items.DIAMOND_BLOCK,
			Items.NETHERITE_BLOCK,
			Items.GOLDEN_APPLE,
			Items.ENCHANTED_GOLDEN_APPLE,
			Items.ENDER_PEARL,
			Items.WIND_CHARGE,
			Items.TOTEM_OF_UNDYING,
			Items.ELYTRA,
			Items.TNT
	};

	private final SettingGroup sgGeneral = settings.createGroup();

	private final ItemPriceSetting items = sgGeneral.add(new ItemPriceSetting
			.Builder()
			.id("ft-items")
			.availableItems(FunTimeItems.getList())
			.availableItems(funTimeItems)
			.build());

	private final BoolSetting active = sgGeneral.add(new BoolSetting
			.Builder()
			.id("active")
			.defaultValue(false)
			.build());

	private final BoolSetting sell = sgGeneral.add(new BoolSetting
			.Builder()
			.id("sell")
			.defaultValue(false)
			.build());

	private final BoolSetting interfaceInAuction = sgGeneral.add(new BoolSetting
			.Builder()
			.id("interface-in-auction")
			.defaultValue(true)
			.build());

	private final SettingGroup sgDelays = settings.createGroup();

	private final DoubleSetting restartDelay = sgDelays.add(new DoubleSetting
			.Builder()
			.id("restart-delay")
			.sliderRange(10, 20)
			.range(0, 50)
			.defaultValue(10.5)
			.suffix(Suffix.SECONDS)
			.build());

	private final IntRangeSetting updateDelay = sgDelays.add(new IntRangeSetting
			.Builder()
			.id("update-delay")
			.sliderRange(1, 10)
			.range(1, 40)
			.defaultValue(7, 8)
			.onChanged(function::getUpdateDelay)
			.suffix(Suffix.TICKS)
			.build());

	private final IntRangeSetting pickDelay = sgDelays.add(new IntRangeSetting
			.Builder()
			.id("pick-delay")
			.sliderRange(1, 8)
			.range(1, 40)
			.defaultValue(3, 5)
			.onChanged(function::getPickDelay)
			.suffix(Suffix.TICKS)
			.build());

	private final IntRangeSetting payDelay = sgDelays.add(new IntRangeSetting
			.Builder()
			.id("pay-delay")
			.sliderRange(1, 8)
			.range(1, 40)
			.defaultValue(3, 5)
			.onChanged(function::getPayDelay)
			.suffix(Suffix.TICKS)
			.build());

	private final IntRangeSetting sellDelay = sgDelays.add(new IntRangeSetting
			.Builder()
			.id("sell-delay")
			.sliderRange(1, 8)
			.range(1, 40)
			.defaultValue(3, 5)
			.onChanged(function::getSellDelay)
			.suffix(Suffix.TICKS)
			.build());

	private final IntSetting lagTime = sgDelays.add(new IntSetting
			.Builder()
			.id("lag-time")
			.sliderRange(100, 600)
			.range(10, 2000)
			.defaultValue(500)
			.onChanged(function::getUpdateDelay)
			.suffix(Suffix.MILLISECONDS)
			.build());

	private final SettingGroup sgAnarchy = settings.createGroup();

	public final IntListSetting anarchy = sgAnarchy.add(new IntListSetting
			.Builder()
			.id("anarchy")
			.min(10)
			.max(1000)
			.defaultValue(13, 14, 15)
			.build());

	public AutoBuyConfig(AutoBuy function) {
		super(function);
	}
}
