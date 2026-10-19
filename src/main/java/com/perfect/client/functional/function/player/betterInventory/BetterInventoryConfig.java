package com.perfect.client.functional.function.player.betterInventory;

import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BindSetting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.list.ItemsSetting;
import com.perfect.client.core.functional.setting.impl.number.IntSetting;
import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

@Getter
public class BetterInventoryConfig extends FunctionConfig<BetterInventory> {

	private static final Item[] carryItems = new Item[]{
			Items.ENDER_CHEST,
			Items.OBSIDIAN,
			Items.END_CRYSTAL,
			Items.ENDER_PEARL,
			Items.EXPERIENCE_BOTTLE,
			Items.TOTEM_OF_UNDYING,
			Items.GOLDEN_APPLE,
			Items.ENCHANTED_GOLDEN_APPLE,
			Items.NETHERITE_HELMET,
			Items.NETHERITE_CHESTPLATE,
			Items.NETHERITE_LEGGINGS,
			Items.NETHERITE_BOOTS,
			Items.DIAMOND_HELMET,
			Items.DIAMOND_CHESTPLATE,
			Items.DIAMOND_LEGGINGS,
			Items.DIAMOND_BOOTS
	};

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgAutoXCarry = settings.createGroup();
	private final SettingGroup sgTooltips = settings.createGroup();

	private final BoolSetting xCarry = sgGeneral.add(new BoolSetting
			.Builder()
			.id("x-carry")
			.defaultValue(false)
			.onChanged(function::onToggled)
			.build());

	private final BoolSetting autoXCarry = sgAutoXCarry.add(new BoolSetting
			.Builder()
			.id("auto-x-carry")
			.defaultValue(false)
			.build());

	private final IntSetting carryDelay = sgAutoXCarry.add(new IntSetting
			.Builder()
			.id("carry-delay")
			.defaultValue(3)
			.sliderRange(0, 5)
			.build());

	private final ItemsSetting itemsToCarry = sgAutoXCarry.add(new ItemsSetting
			.Builder()
			.id("items-to-carry")
			.availableItems(carryItems)
			.visible(autoXCarry::get)
			.build());


	private final BoolSetting tooltips = sgTooltips.add(new BoolSetting
			.Builder()
			.id("tooltips")
			.defaultValue(false)
			.onChanged(value -> {
				function.updateTooltips = true;
				function.onToggled();
			})
			.build());

	private final BindSetting keybind = sgTooltips.add(new BindSetting
			.Builder()
			.id("keybind")
			.defaultValue(Bind.fromKey(GLFW.GLFW_KEY_LEFT_ALT))
			.toggleable(false)
			.onChanged(value -> function.updateTooltips = true)
			.visible(tooltips::get)
			.build());

	private final EnumListSetting<BetterInventory.Previews> previews = sgTooltips.add(new EnumListSetting
			.Builder<>(BetterInventory.Previews.values())
			.id("condition")
			.defaultValue(BetterInventory.Previews.values())
			.onChanged(value -> function.updateTooltips = true)
			.visible(tooltips::get)
			.build());

	public BetterInventoryConfig(BetterInventory function) {
		super(function);
	}

}
