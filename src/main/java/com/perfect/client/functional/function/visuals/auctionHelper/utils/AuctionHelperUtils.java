package com.perfect.client.functional.function.visuals.auctionHelper.utils;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.game.ItemUtils;
import com.perfect.client.core.utils.range.IntRange;
import com.perfect.client.functional.function.visuals.auctionHelper.AuctionHelper;
import com.perfect.client.functional.function.visuals.auctionHelper.AuctionHelperConfig;
import com.perfect.client.functional.function.visuals.auctionHelper.enums.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.ItemTags;

public class AuctionHelperUtils extends FunctionHelper<AuctionHelper, AuctionHelperConfig> {

	private ItemEnchantmentsComponent enchantments;

	public AuctionHelperUtils(AuctionHelper function, AuctionHelperConfig config) {
		super(function, config);
	}

	public boolean isValidItem(ItemStack stack) {
		if (mc.world == null) {
			return false;
		}

		if (config.getCheckDurability().get() && stack.isDamaged()) {
			return false;
		}

		enchantments = stack.getEnchantments();

		if (ItemUtils.isArmor(stack) && config.getArmor().get()) {
			return isValidArmor();
		}
		else if (stack.isIn(ItemTags.SWORDS) && config.getSwords().get()) {
			return isValidSword();
		}
		else if (stack.isIn(ItemTags.PICKAXES) && config.getPickaxes().get()) {
			return isValidPickaxe();
		}
		else if (stack.getItem() instanceof PotionItem && config.getPotions().get()) {
			PotionContentsComponent potionContents = stack.get(DataComponentTypes.POTION_CONTENTS);
			return isValidPotionWithGoodEffects(potionContents) && isValidPotionWithBadEffects(potionContents);
		}

		return true;
	}

	private boolean isValidArmor() {
		if (enchantments.isEmpty()) {
			return config.getArmorEnchantments().get().isEmpty();
		}

		return isValidArmor(ArmorEnchantments.PROTECTION, config.getArmorProtectionLevel().get())
				&& isValidArmor(ArmorEnchantments.UNBREAKING, config.getArmorUnbreakingLevel().get())
				&& isValidArmor(ArmorEnchantments.MENDING, 1, 1)
				&& isValidArmor(ArmorEnchantments.NOT_THORNS, 0, 0);
	}

	private boolean isValidArmor(ArmorEnchantments enchantment, IntRange intRange) {
		return isValidArmor(enchantment, intRange.getFrom(), intRange.getTo());
	}

	private boolean isValidArmor(ArmorEnchantments enchantment, int minLevel, int maxLevel) {
		if (!config.getArmorEnchantments().get(enchantment)) {
			return true;
		}

		return isValidEnchantment(enchantment.getEnchantment(), minLevel, maxLevel);
	}

	private boolean isValidSword() {
		if (enchantments.isEmpty()) {
			return config.getSwordEnchantments().get().isEmpty();
		}

		return isValidSword(SwordEnchantments.SHARPNESS, config.getSwordSharpnessLevel().get())
				&& isValidSword(SwordEnchantments.UNBREAKING, config.getSwordUnbreakingLevel().get())
				&& isValidSword(SwordEnchantments.KNOCKBACK, config.getSwordKnockbackLevel().get())
				&& isValidSword(SwordEnchantments.FIRE_ASPECT, config.getSwordFireAspectLevel().get())
				&& isValidSword(SwordEnchantments.SWEEPING_EDGE, config.getSwordSweepingEdgeLevel().get())
				&& isValidSword(SwordEnchantments.LOOTING, config.getSwordLootingLevel().get())
				&& isValidSword(SwordEnchantments.MENDING, 1, 1);
	}

	private boolean isValidSword(SwordEnchantments enchantment, IntRange intRange) {
		return isValidSword(enchantment, intRange.getFrom(), intRange.getTo());
	}

	private boolean isValidSword(SwordEnchantments enchantment, int minLevel, int maxLevel) {
		if (!config.getSwordEnchantments().get(enchantment)) {
			return true;
		}

		return isValidEnchantment(enchantment.getEnchantment(), minLevel, maxLevel);
	}

	private boolean isValidPickaxe() {
		if (enchantments.isEmpty()) {
			return config.getPickaxeEnchantments().get().isEmpty();
		}

		return isValidPickaxe(PickaxeEnchantments.EFFICIENCY, config.getPickaxeEfficiencyLevel().get())
				&& isValidPickaxe(PickaxeEnchantments.UNBREAKING, config.getPickaxeUnbreakingLevel().get())
				&& isValidPickaxe(PickaxeEnchantments.FORTUNE, config.getPickaxeFortuneLevel().get())
				&& isValidPickaxe(PickaxeEnchantments.SILK_TOUCH, 1, 1)
				&& isValidPickaxe(PickaxeEnchantments.MENDING, 1, 1);
	}

	private boolean isValidPickaxe(PickaxeEnchantments enchantment, IntRange intRange) {
		return isValidPickaxe(enchantment, intRange.getFrom(), intRange.getTo());
	}

	private boolean isValidPickaxe(PickaxeEnchantments enchantment, int minLevel, int maxLevel) {
		if (!config.getPickaxeEnchantments().get(enchantment)) {
			return true;
		}

		return isValidEnchantment(enchantment.getEnchantment(), minLevel, maxLevel);
	}

	private boolean isValidPotionWithGoodEffects(PotionContentsComponent potionContents) {
		if (potionContents == null) {
			return false;
		}

		boolean invisibilityCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.INVISIBILITY)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.INVISIBILITY.value(),
				1,
				1
		);

		boolean fireResistanceCheck =
				!config.getPotionGoodEffects().get(GoodPotionsEffects.FIRE_RESISTANCE)
						|| isValidPotionEffect(
						potionContents,
						StatusEffects.FIRE_RESISTANCE.value(),
						1,
						1
				);

		boolean speedCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.SPEED)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.SPEED.value(),
				config.getPotionSpeedLevel().getFrom(),
				config.getPotionSpeedLevel().getTo()
		);

		boolean instantHealthCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.INSTANT_HEALTH)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.INSTANT_HEALTH.value(),
				config.getPotionInstantHealthLevel().getFrom(),
				config.getPotionInstantHealthLevel().getTo()
		);

		boolean regenerationCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.REGENERATION)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.REGENERATION.value(),
				1,
				1
		);

		boolean strengthCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.STRENGTH)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.STRENGTH.value(),
				config.getPotionStrengthLevel().getFrom(),
				config.getPotionStrengthLevel().getTo()
		);

		boolean resistanceCheck = !config.getPotionGoodEffects().get(GoodPotionsEffects.RESISTANCE)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.RESISTANCE.value(),
				config.getPotionResistanceLevel().getFrom(),
				config.getPotionResistanceLevel().getTo()
		);

		return invisibilityCheck && fireResistanceCheck && speedCheck && instantHealthCheck && regenerationCheck
				&& strengthCheck && resistanceCheck;
	}

	private boolean isValidPotionWithBadEffects(PotionContentsComponent potionContents) {
		if (potionContents == null) {
			return false;
		}

		boolean slownessCheck = !config.getPotionBadEffects().get(BadPotionsEffects.SLOWNESS)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.SLOWNESS.value(),
				config.getPotionSlownessLevel().getFrom(),
				config.getPotionSlownessLevel().getTo()
		);

		boolean poisonCheck = !config.getPotionBadEffects().get(BadPotionsEffects.POISON)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.POISON.value(),
				1,
				1
		);

		boolean weaknessCheck = !config.getPotionBadEffects().get(BadPotionsEffects.WEAKNESS)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.WEAKNESS.value(),
				config.getPotionWeaknessLevel().getFrom(),
				config.getPotionWeaknessLevel().getTo()
		);

		boolean witherCheck = !config.getPotionBadEffects().get(BadPotionsEffects.WITHER)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.WITHER.value(),
				1,
				1
		);

		boolean nauseaCheck = !config.getPotionBadEffects().get(BadPotionsEffects.NAUSEA)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.NAUSEA.value(),
				1,
				1
		);

		boolean blindnessCheck = !config.getPotionBadEffects().get(BadPotionsEffects.BLINDNESS)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.BLINDNESS.value(),
				1,
				1
		);

		boolean hungerCheck = !config.getPotionBadEffects().get(BadPotionsEffects.HUNGER)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.HUNGER.value(),
				1,
				1
		);

		boolean glowingCheck = !config.getPotionBadEffects().get(BadPotionsEffects.GLOWING)
				|| isValidPotionEffect(
				potionContents,
				StatusEffects.GLOWING.value(),
				1,
				1
		);

		return slownessCheck && poisonCheck && weaknessCheck && witherCheck && nauseaCheck && blindnessCheck
				&& hungerCheck && glowingCheck;
	}

	private boolean isValidPotionEffect(
			PotionContentsComponent potionContents,
			StatusEffect effectType,
			int minLevel,
			int maxLevel
	) {
		for (StatusEffectInstance effectInstance : potionContents.getEffects()) {
			if (effectInstance.getEffectType().value() == effectType) {
				int amplifier = effectInstance.getAmplifier();
				boolean minCheck = amplifier >= (minLevel - 1);

				if (minLevel == maxLevel) {
					return minCheck;
				}

				if (minCheck && amplifier <= (maxLevel - 1)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isValidEnchantment(RegistryKey<Enchantment> enchantment, int minLevel, int maxLevel) {
		int enchantmentLevel = enchantments.getLevel(EnchantmentUtils.getEntry(enchantment));

		return enchantmentLevel >= minLevel && enchantmentLevel <= maxLevel;
	}
}
