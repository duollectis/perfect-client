package com.perfect.client.core.utils.math.damage;

import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

import static com.perfect.client.PerfectClient.mc;


public class DamageUtils {

	public static float getDamageLeft(LivingEntity target, float damage) {
		if (mc.world == null)
			return 0f;

		return DamageUtil.getDamageLeft(
				target,
				damage,
				mc.world.getDamageSources().explosion(null),
				target.getArmor(),
				(float) target.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS)
		);
	}

	public static float modifyDamageByDifficulty(float damage) {
		if (mc.world == null)
			return 0f;

		return switch (mc.world.getDifficulty()) {
			case HARD -> damage * 1.5f;
			case PEACEFUL -> 0f;
			case EASY -> damage;
			case NORMAL -> damage < 2f ? damage : damage * 0.5f + 1f;
		};
	}

	public static float modifyDamageByResistance(LivingEntity entity, float damage) {
		StatusEffectInstance resistance = entity.getStatusEffect(StatusEffects.RESISTANCE);

		if (resistance == null) {
			return damage;
		}

		return Math.max((float) (25 - (resistance.getAmplifier() + 1) * 5) * damage * 0.04f, 0.0f);
	}

	public static float modifyDamageByProtection(LivingEntity entity, float damage) {
		float protection = getProtectionAmount(EntityUtils.getArmor(entity));

		if (protection < 1) {
			return damage;
		}

		return DamageUtil.getInflictedDamage(damage, protection);
	}

	public static int getProtectionAmount(ItemStack[] equipment) {
		int protection = 0;

		for (ItemStack stack : equipment) {
			protection += getProtectionAmount(stack);
		}

		return protection;
	}

	public static int getProtectionAmount(ItemStack stack) {
		int modifierBlast = EnchantmentUtils.getLevel(stack, Enchantments.BLAST_PROTECTION);
		int modifier = EnchantmentUtils.getLevel(stack, Enchantments.PROTECTION);
		return modifierBlast * 2 + modifier;
	}
}
