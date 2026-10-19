package com.perfect.client.functional.function.visuals.auctionHelper;

import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.setting.SettingGroup;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.impl.color.ColorSetting;
import com.perfect.client.core.functional.setting.impl.enums.EnumListSetting;
import com.perfect.client.core.functional.setting.impl.number.range.IntRangeSetting;
import com.perfect.client.functional.function.visuals.auctionHelper.enums.*;
import lombok.Getter;

import java.awt.*;

@Getter
public class AuctionHelperConfig extends FunctionConfig<AuctionHelper> {

	private final SettingGroup sgGeneral = settings.createGroup();
	private final SettingGroup sgArmor = settings.createGroup();
	private final SettingGroup sgSword = settings.createGroup();
	private final SettingGroup sgPickaxe = settings.createGroup();
	private final SettingGroup sgPotions = settings.createGroup();

	private final ColorSetting minItemPriceColor = sgGeneral.add(new ColorSetting
			.Builder()
			.id("min-item-price-color")
			.defaultValue(Color.GREEN)
			.build());

	private final ColorSetting bestItemPriceColor = sgGeneral.add(new ColorSetting
			.Builder()
			.id("best-item-price-color")
			.defaultValue(Color.RED)
			.build());

	private final BoolSetting checkDurability = sgGeneral.add(new BoolSetting
			.Builder()
			.id("check-durability")
			.defaultValue(false)
			.build());

	private final BoolSetting armor = sgArmor.add(new BoolSetting
			.Builder()
			.id("armor")
			.defaultValue(false)
			.build());

	private final EnumListSetting<ArmorEnchantments> armorEnchantments = sgArmor.add(new EnumListSetting
			.Builder<>(ArmorEnchantments.values())
			.id("armor-enchantments")
			.visible(armor::get)
			.build());

	private final IntRangeSetting armorProtectionLevel = sgArmor.add(new IntRangeSetting
			.Builder()
			.id("protection-level")
			.defaultValue(4, 5)
			.sliderRange(1, 6)
			.visible(() -> armor.get() && armorEnchantments.get(ArmorEnchantments.PROTECTION))
			.build());

	private final IntRangeSetting armorUnbreakingLevel = sgArmor.add(new IntRangeSetting
			.Builder()
			.id("unbreaking-level")
			.defaultValue(4, 5)
			.sliderRange(1, 7)
			.visible(() -> armor.get() && armorEnchantments.get(ArmorEnchantments.UNBREAKING))
			.build());

	private final BoolSetting swords = sgSword.add(new BoolSetting
			.Builder()
			.id("swords")
			.defaultValue(false)
			.build());

	private final EnumListSetting<SwordEnchantments> swordEnchantments = sgSword.add(new EnumListSetting
			.Builder<>(SwordEnchantments.values())
			.id("sword-enchantments")
			.visible(swords::get)
			.build());

	private final IntRangeSetting swordSharpnessLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("sharpness-level")
			.defaultValue(5, 7)
			.sliderRange(1, 10)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.SHARPNESS))
			.build());

	private final IntRangeSetting swordUnbreakingLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("unbreaking-level")
			.defaultValue(4, 5)
			.sliderRange(1, 7)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.UNBREAKING))
			.build());

	private final IntRangeSetting swordKnockbackLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("knockback-level")
			.defaultValue(1, 2)
			.sliderRange(1, 2)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.KNOCKBACK))
			.build());

	private final IntRangeSetting swordFireAspectLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("fire-aspect-level")
			.defaultValue(1, 2)
			.sliderRange(1, 2)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.FIRE_ASPECT))
			.build());

	private final IntRangeSetting swordSweepingEdgeLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("sweeping-edge-level")
			.defaultValue(1, 3)
			.sliderRange(1, 3)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.SWEEPING_EDGE))
			.build());

	private final IntRangeSetting swordLootingLevel = sgSword.add(new IntRangeSetting
			.Builder()
			.id("looting-level")
			.defaultValue(3, 5)
			.sliderRange(1, 7)
			.visible(() -> swords.get() && swordEnchantments.get(SwordEnchantments.LOOTING))
			.build());


	private final BoolSetting pickaxes = sgPickaxe.add(new BoolSetting
			.Builder()
			.id("pickaxes")
			.defaultValue(false)
			.build());

	private final EnumListSetting<PickaxeEnchantments> pickaxeEnchantments = sgPickaxe.add(new EnumListSetting
			.Builder<>(PickaxeEnchantments.values())
			.id("pickaxe-enchantments")
			.visible(pickaxes::get)
			.build());

	private final IntRangeSetting pickaxeEfficiencyLevel = sgPickaxe.add(new IntRangeSetting
			.Builder()
			.id("efficiency-level")
			.defaultValue(3, 5)
			.sliderRange(1, 10)
			.visible(() -> pickaxes.get() && pickaxeEnchantments.get(PickaxeEnchantments.EFFICIENCY))
			.build());

	private final IntRangeSetting pickaxeUnbreakingLevel = sgPickaxe.add(new IntRangeSetting
			.Builder()
			.id("unbreaking-level")
			.defaultValue(3, 5)
			.sliderRange(1, 7)
			.visible(() -> pickaxes.get() && pickaxeEnchantments.get(PickaxeEnchantments.UNBREAKING))
			.build());

	private final IntRangeSetting pickaxeFortuneLevel = sgPickaxe.add(new IntRangeSetting
			.Builder()
			.id("fortune-level")
			.defaultValue(3, 5)
			.sliderRange(1, 7)
			.visible(() -> pickaxes.get() && pickaxeEnchantments.get(PickaxeEnchantments.FORTUNE))
			.build());


	private final BoolSetting potions = sgPotions.add(new BoolSetting
			.Builder()
			.id("potions")
			.defaultValue(false)
			.build());

	private final EnumListSetting<GoodPotionsEffects> potionGoodEffects = sgPotions.add(new EnumListSetting
			.Builder<>(GoodPotionsEffects.values())
			.id("good-potion-effects")
			.visible(potions::get)
			.build());

	private final IntRangeSetting potionSpeedLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("speed-level")
			.defaultValue(2, 3)
			.sliderRange(1, 3)
			.visible(() -> potions.get() && potionGoodEffects.get(GoodPotionsEffects.SPEED))
			.build());

	private final IntRangeSetting potionInstantHealthLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("instant-health-level")
			.defaultValue(1, 2)
			.sliderRange(1, 2)
			.visible(() -> potions.get() && potionGoodEffects.get(GoodPotionsEffects.INSTANT_HEALTH))
			.build());

	private final IntRangeSetting potionStrengthLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("strength-level")
			.defaultValue(1, 5)
			.sliderRange(1, 5)
			.visible(() -> potions.get() && potionGoodEffects.get(GoodPotionsEffects.STRENGTH))
			.build());

	private final IntRangeSetting potionResistanceLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("resistance-level")
			.defaultValue(3, 4)
			.sliderRange(1, 4)
			.visible(() -> potions.get() && potionGoodEffects.get(GoodPotionsEffects.RESISTANCE))
			.build());

	private final EnumListSetting<BadPotionsEffects> potionBadEffects = sgPotions.add(new EnumListSetting
			.Builder<>(BadPotionsEffects.values())
			.id("bad-potion-effects")
			.visible(potions::get)
			.build());

	private final IntRangeSetting potionSlownessLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("slowness-level")
			.defaultValue(3, 4)
			.sliderRange(1, 6)
			.visible(() -> potions.get() && potionBadEffects.get(BadPotionsEffects.SLOWNESS))
			.build());

	private final IntRangeSetting potionWeaknessLevel = sgPotions.add(new IntRangeSetting
			.Builder()
			.id("weakness-level")
			.defaultValue(3, 4)
			.sliderRange(1, 4)
			.visible(() -> potions.get() && potionBadEffects.get(BadPotionsEffects.WEAKNESS))
			.build());

	public AuctionHelperConfig(AuctionHelper function) {
		super(function);
	}
}
