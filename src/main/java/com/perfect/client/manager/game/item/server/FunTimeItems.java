package com.perfect.client.manager.game.item.server;

import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.core.utils.itemIdentifier.custom.CustomItemIdentifier;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.attributeModifiers.Attribute;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.attributeModifiers.AttributeModifiersChecker;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.enchantment.EnchantmentChecker;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.enchantment.EnchantmentLevel;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.lore.LoreChecker;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.lore.OriginalItemChecker;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.potionContents.PotionContentsChecker;
import com.perfect.client.core.utils.itemIdentifier.custom.checkers.potionContents.StatusEffectLevel;
import com.perfect.client.manager.game.item.ItemManager;
import lombok.Getter;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;


public class FunTimeItems {

	@Getter
	private static final List<ItemIdentifier> list = new ArrayList<>();

	private static ItemIdentifier add(ItemIdentifier itemIdentifier) {
		list.add(itemIdentifier);
		return ItemManager.add(itemIdentifier);
	}

	public static ItemIdentifier TNT_TIER_WHITE = add(new CustomItemIdentifier(
			Items.TNT, "ft:tnt-tier-white", "TNT - TIER WHITE", false,
			new LoreChecker(
					"Этот динамит взрывается",
					"в 10 раз сильнее обычного"
			)
	));

	public static ItemIdentifier TNT_TIER_BLACK = add(new CustomItemIdentifier(
			Items.TNT, "ft:tnt-tier-black", "TNT - TIER BLACK", false,
			new LoreChecker(
					"Этот динамит взрывается",
					"в 10 раз сильнее обычного",
					"и способен взорвать обсидиан"
			)
	));

	public static ItemIdentifier TRAP = add(new CustomItemIdentifier(
			Items.NETHERITE_SCRAP, "ft:trap", "Трапка", false,
			new LoreChecker("Каст:", "Нерушимая клетка", "Длительность:", "15 секунд")
	));

	public static ItemIdentifier LAYER = add(new CustomItemIdentifier(
			Items.DRIED_KELP, "ft:layer", "Пласт", false,
			new LoreChecker(
					"Каст:", "Нерушимая стена",
					"Длительность:",
					"Вертикальный:", "20 секунд",
					"Горизонтальный:", "60 секунд"
			)
	));

	public static ItemIdentifier FIRE_TORNADO = add(new CustomItemIdentifier(
			Items.FIRE_CHARGE, "ft:fire-tornado", "Огненный Смерч", false,
			new LoreChecker("Каст:", "Огненная волна", "Радиус:", "10 блоков")
	));

	public static ItemIdentifier GOD_AURA = add(new CustomItemIdentifier(
			Items.PHANTOM_MEMBRANE, "ft:god-aura", "Божья Аура", false,
			new LoreChecker("Каст:", "Божественная аура", "Радиус:", "2 блока")
	));

	public static ItemIdentifier VISIBLE_DUST = add(new CustomItemIdentifier(
			Items.SUGAR, "ft:visible-dust", "Явная Пыль", false,
			new LoreChecker("Каст:", "Световая вспышка", "Радиус:", "10 блоков")
	));

	public static ItemIdentifier DISORIENTATION = add(new CustomItemIdentifier(
			Items.ENDER_EYE, "ft:disorientation", "Дезориентация", false,
			new LoreChecker("Каст:", "Звуковая волка", "Радиус:", "10 блоков")
	));

	public static ItemIdentifier FREEZING_SNOWBALL = add(new CustomItemIdentifier(
			Items.SNOWBALL, "ft:freezing-snowball", "Снежок заморозка", false,
			new LoreChecker("Каст:", "Ледяная сфера", "Радиус:", "7 блоков")
	));

	public static ItemIdentifier SINISTER_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:sinister-skin", "Зловещий скин", true,
			new LoreChecker("Зловещий ", "скин взамен")
	));

	public static ItemIdentifier PEARL_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:pearl-skin", "Жемчужный скин", true,
			new LoreChecker("Жемчужный ", "скин взамен")
	));

	public static ItemIdentifier CRUEL_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:cruel-skin", "Жестокий скин", true,
			new LoreChecker("Жестокий ", "скин взамен")
	));

	public static ItemIdentifier INSIDIOUS_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:insidious-skin", "Коварный скин", true,
			new LoreChecker("Коварный ", "скин взамен")
	));

	public static ItemIdentifier NIGHTMARISH_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:nightmarish-skin", "Кошмарный скин", true,
			new LoreChecker("Кошмарный ", "скин взамен")
	));

	public static ItemIdentifier ICE_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:ice-skin", "Ледяной скин", true,
			new LoreChecker("Ледяной ", "скин взамен")
	));

	public static ItemIdentifier SINGULAR_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:singular-skin", "Сингулярный скин", true,
			new LoreChecker("Сингулярный ", "скин взамен")
	));

	public static ItemIdentifier INEVITABLE_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:inevitable-skin", "Неизбежный скин", true,
			new LoreChecker("Неизбежный ", "скин взамен")
	));

	public static ItemIdentifier DEATH_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:death-skin", "Смертельный скин", true,
			new LoreChecker("Смертельный ", "скин взамен")
	));

	public static ItemIdentifier DRAGON_SKIN = add(new CustomItemIdentifier(
			Items.PAPER, "ft:dragon-skin", "Драконий скин", true,
			new LoreChecker("Драконий ", "скин взамен")
	));

	public static ItemIdentifier GOD_TOUCH = add(new CustomItemIdentifier(
			Items.GOLDEN_PICKAXE, "ft:god-touch", "Божье касание", true,
			new LoreChecker("Божье касание")
	));

	public static ItemIdentifier POWERFUL_HIT = add(new CustomItemIdentifier(
			Items.GOLDEN_PICKAXE, "ft:powerful-hit", "Мощный удар", true,
			new LoreChecker("Мощный удар")
	));

	public static ItemIdentifier CRUSHER_TRIDENT = add(new CustomItemIdentifier(
			Items.TRIDENT, "ft:crusher-trident", "Трезубец Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.CHANNELING, 1),
					new EnchantmentLevel(Enchantments.SHARPNESS, 7),
					new EnchantmentLevel(Enchantments.IMPALING, 5),
					new EnchantmentLevel(Enchantments.FIRE_ASPECT, 2),
					new EnchantmentLevel(Enchantments.LOYALTY, 3),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			),
			new LoreChecker(
					"Скаут III",
					"Опытный III",
					"Вампиризм II",
					"Ступор III",
					"Притяжение II",
					"Окисление II",
					"Возвращение",
					"Подрывник",
					"Яд III",
					"Детекция III"
			),
			new OriginalItemChecker()
	));

	public static ItemIdentifier CRUSHER_MACE = add(new CustomItemIdentifier(
			Items.MACE, "ft:crusher-mace", "Булава Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.SHARPNESS, 7),
					new EnchantmentLevel(Enchantments.SMITE, 7),
					new EnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, 7),
					new EnchantmentLevel(Enchantments.DENSITY, 5),
					new EnchantmentLevel(Enchantments.BREACH, 3),
					new EnchantmentLevel(Enchantments.SWEEPING_EDGE, 3),
					new EnchantmentLevel(Enchantments.FIRE_ASPECT, 2),
					new EnchantmentLevel(Enchantments.LOOTING, 5),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			),
			new LoreChecker(
					"Опытный III",
					"Вампиризм II",
					"Окисление II",
					"Яд III",
					"Детекция III"
			),
			new OriginalItemChecker()
	));

	public static ItemIdentifier CRUSHER_SWORD = add(new CustomItemIdentifier(
			Items.NETHERITE_SWORD, "ft:crusher-sword", "Меч Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.SHARPNESS, 7),
					new EnchantmentLevel(Enchantments.SMITE, 7),
					new EnchantmentLevel(Enchantments.BANE_OF_ARTHROPODS, 7),
					new EnchantmentLevel(Enchantments.SWEEPING_EDGE, 3),
					new EnchantmentLevel(Enchantments.FIRE_ASPECT, 2),
					new EnchantmentLevel(Enchantments.LOOTING, 5),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			),
			new LoreChecker(
					"Опытный III",
					"Вампиризм II",
					"Окисление II",
					"Яд III",
					"Детекция III"
			),
			new OriginalItemChecker()
	));

	public static ItemIdentifier CRUSHER_CROSSBOW = add(new CustomItemIdentifier(
			Items.CROSSBOW, "ft:crusher-crossbow", "Арбалет Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.PIERCING, 5),
					new EnchantmentLevel(Enchantments.MULTISHOT, 1),
					new EnchantmentLevel(Enchantments.QUICK_CHARGE, 3),
					new EnchantmentLevel(Enchantments.UNBREAKING, 3),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			),
			new OriginalItemChecker()
	));

	public static ItemIdentifier CRUSHER_PICKAXE = add(new CustomItemIdentifier(
			Items.NETHERITE_PICKAXE, "ft:crusher-pickaxe", "Кирка Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.FORTUNE, 5),
					new EnchantmentLevel(Enchantments.EFFICIENCY, 10),
					new EnchantmentLevel(Enchantments.MENDING, 1),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5)
			),
			new LoreChecker(
					"Бульдозер II",
					"Опытный III",
					"Магнит",
					"Авто-Плавка",
					"Паутина",
					"Пингер"
			)
	));

	public static ItemIdentifier CRUSHER_HELMET = add(new CustomItemIdentifier(
			Items.NETHERITE_HELMET, "ft:crusher-helmet", "Шлем Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.PROTECTION, 5),
					new EnchantmentLevel(Enchantments.BLAST_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.FIRE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.RESPIRATION, 3),
					new EnchantmentLevel(Enchantments.AQUA_AFFINITY, 1),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			)
	));

	public static ItemIdentifier CRUSHER_CHESTPLATE = add(new CustomItemIdentifier(
			Items.NETHERITE_CHESTPLATE, "ft:crusher-chestplate", "Нагрудник Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.PROTECTION, 5),
					new EnchantmentLevel(Enchantments.BLAST_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.FIRE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			)
	));

	public static ItemIdentifier CRUSHER_LEGGINGS = add(new CustomItemIdentifier(
			Items.NETHERITE_LEGGINGS, "ft:crusher-leggings", "Поножи Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.PROTECTION, 5),
					new EnchantmentLevel(Enchantments.BLAST_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.FIRE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			)
	));

	public static ItemIdentifier CRUSHER_BOOTS = add(new CustomItemIdentifier(
			Items.NETHERITE_BOOTS, "ft:crusher-boots", "Ботинки Крушителя", true,
			new EnchantmentChecker(
					new EnchantmentLevel(Enchantments.PROTECTION, 5),
					new EnchantmentLevel(Enchantments.BLAST_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.FIRE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.PROJECTILE_PROTECTION, 5),
					new EnchantmentLevel(Enchantments.FEATHER_FALLING, 4),
					new EnchantmentLevel(Enchantments.SOUL_SPEED, 3),
					new EnchantmentLevel(Enchantments.DEPTH_STRIDER, 3),
					new EnchantmentLevel(Enchantments.UNBREAKING, 5),
					new EnchantmentLevel(Enchantments.MENDING, 1)
			)
	));

	public static ItemIdentifier HOLY_WATER = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:holy-water", "Святая Вода", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.REGENERATION, 1200, 3),
					new StatusEffectLevel(StatusEffects.INVISIBILITY, 12000, 2),
					new StatusEffectLevel(StatusEffects.INSTANT_HEALTH, 0, 2)
			)
	));

	public static ItemIdentifier POTION_OF_ANGER = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:potion-of-anger", "Зелье Гнева", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.STRENGTH, 600, 5),
					new StatusEffectLevel(StatusEffects.SLOWNESS, 600, 4)
			)
	));

	public static ItemIdentifier PALADIN_POTION = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:paladin-potion", "Зелье Паладина", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.RESISTANCE, 12000, 1),
					new StatusEffectLevel(StatusEffects.FIRE_RESISTANCE, 12000, 1),
					new StatusEffectLevel(StatusEffects.INVISIBILITY, 18000, 1),
					new StatusEffectLevel(StatusEffects.HEALTH_BOOST, 1200, 3)
			)
	));

	public static ItemIdentifier ASSASSIN_POTION = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:assassin-potion", "Зелье Ассасина", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.STRENGTH, 1200, 4),
					new StatusEffectLevel(StatusEffects.SPEED, 600, 3),
					new StatusEffectLevel(StatusEffects.HASTE, 1200, 1),
					new StatusEffectLevel(StatusEffects.INSTANT_DAMAGE, 1, 2)
			)
	));

	public static ItemIdentifier POTION_OF_RADIATION = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:potion-of-radiation", "Зелье Радиации", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.POISON, 1200, 2),
					new StatusEffectLevel(StatusEffects.WITHER, 1200, 2),
					new StatusEffectLevel(StatusEffects.SLOWNESS, 1800, 3),
					new StatusEffectLevel(StatusEffects.HUNGER, 1200, 5),
					new StatusEffectLevel(StatusEffects.GLOWING, 2400, 1)
			)
	));

	public static ItemIdentifier HYPNOTIC = add(new CustomItemIdentifier(
			Items.SPLASH_POTION, "ft:hypnotic", "Снотворное", true,
			new PotionContentsChecker(
					new StatusEffectLevel(StatusEffects.WEAKNESS, 1800, 2),
					new StatusEffectLevel(StatusEffects.MINING_FATIGUE, 200, 2),
					new StatusEffectLevel(StatusEffects.WITHER, 1800, 3),
					new StatusEffectLevel(StatusEffects.BLINDNESS, 200, 1)
			)
	));

	public static ItemIdentifier PUNISHER_TALISMAN = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:punisher-talisman", "Талисман Карателя", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 7.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, -4.0),
					Attribute.addPercent(EntityAttributes.MAX_HEALTH, 0.1)
			)
	));

	public static ItemIdentifier CRUSHER_TALISMAN = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:crusher-talisman", "Талисман Крушителя", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 4.0),
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 3.0),
					Attribute.addValue(EntityAttributes.ARMOR_TOUGHNESS, 2.0),
					Attribute.addValue(EntityAttributes.ARMOR, 2.0)
			)
	));

	public static ItemIdentifier TALISMAN_OF_DISCORD = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:talisman-of-discord", "Талисман Раздора", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 4.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 2.0),
					Attribute.addPercent(EntityAttributes.MOVEMENT_SPEED, 0.1),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.1)
			)
	));

	public static ItemIdentifier TYRANT_TALISMAN = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:tyrant_talisman", "Талисман Тирана", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 2.0),
					Attribute.addValue(EntityAttributes.ARMOR, 2.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, -4.0)
			)
	));

	public static ItemIdentifier TALISMAN_OF_RAGE = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:talisman-of-rage", "Талисман Ярости", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 5.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, -4.0)
			)
	));

	public static ItemIdentifier TALISMAN_OF_WHIRLWIND = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:talisman-of-whirlwind", "Талисман Вихря", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 2.0),
					Attribute.addPercent(EntityAttributes.MOVEMENT_SPEED, 0.15),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.15)
			)
	));

	public static ItemIdentifier TALISMAN_OF_DARKNESS = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:talisman-of-darkness", "Талисман Мрака", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ARMOR, 1.5),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 1.5)
			)
	));

	public static ItemIdentifier DEMON_TALISMAN = add(new CustomItemIdentifier(
			Items.TOTEM_OF_UNDYING, "ft:demon-talisman", "Талисман Демона", true,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 2.5),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.1)
			)
	));

	public static ItemIdentifier SPHERE_OF_CHAOS = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:sphere-of-chaos", "Сфера Хаоса", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.MAX_HEALTH, -4.0),
					Attribute.addValue(EntityAttributes.ARMOR, 2.0),
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 3.0),
					Attribute.addPercent(EntityAttributes.MOVEMENT_SPEED, 0.07),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.13),
					Attribute.addPercent(EntityAttributes.GRAVITY, 0.09)
			)
	));

	public static ItemIdentifier SATYR_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:satyr-sphere", "Сфера Сатира", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 2.0),
					Attribute.addPercent(EntityAttributes.JUMP_STRENGTH, -0.1),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.15)
			)
	));

	public static ItemIdentifier BEAST_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:beast-sphere", "Сфера Бестии", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ARMOR, 1.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 4.0),
					Attribute.addPercent(EntityAttributes.MOVEMENT_SPEED, 0.1),
					Attribute.addPercent(EntityAttributes.ATTACK_SPEED, 0.1)
			)
	));

	public static ItemIdentifier ARES_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:ares-sphere", "Сфера Ареса", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 6.0),
					Attribute.addValue(EntityAttributes.ARMOR, -2.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, -2.0)

			)
	));

	public static ItemIdentifier HYDRA_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:hydra-sphere", "Сфера Гидры", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 4.0),
					Attribute.addValue(EntityAttributes.ARMOR, 2.0),
					Attribute.addPercent(EntityAttributes.SUBMERGED_MINING_SPEED, 0.5),
					Attribute.addPercent(EntityAttributes.OXYGEN_BONUS, 0.5)
			)
	));

	public static ItemIdentifier ICARUS_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:icarus-sphere", "Сфера Икара", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.ATTACK_DAMAGE, 2.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 2.0)
			)
	));

	public static ItemIdentifier ERID_SPHERE = add(new CustomItemIdentifier(
			Items.PLAYER_HEAD, "ft:erid-sphere", "Сфера Эрида", false, 1,
			new AttributeModifiersChecker(
					Attribute.addValue(EntityAttributes.LUCK, 1.0),
					Attribute.addValue(EntityAttributes.MAX_HEALTH, 2.0),
					Attribute.addValue(EntityAttributes.BLOCK_INTERACTION_RANGE, 1.0)
			)
	));

	public static ItemIdentifier CHUNK_LOADER_1X1 = add(new CustomItemIdentifier(
			Items.STRUCTURE_BLOCK, "ft:chunk-loader-1x1", "Прогрузчик чанков [1x1]", false,
			new LoreChecker(
					"Прогружает чанк, в котором",
					"находится этот прогрузчик.",
					"прогружаемой области (1x1)."
			)
	));

	public static ItemIdentifier CHUNK_LOADER_3X3 = add(new CustomItemIdentifier(
			Items.STRUCTURE_BLOCK, "ft:chunk-loader-3x3", "Прогрузчик чанков [3x3]", false,
			new LoreChecker(
					"Прогружает чанк, в котором",
					"находится этот прогрузчик.",
					"прогружаемой области (3x3)."
			)
	));

	public static ItemIdentifier CHUNK_LOADER_5X5 = add(new CustomItemIdentifier(
			Items.STRUCTURE_BLOCK, "ft:chunk-loader-5x5", "Прогрузчик чанков [5x5]", false,
			new LoreChecker(
					"Прогружает чанк, в котором",
					"находится этот прогрузчик.",
					"прогружаемой области (5x5)."
			)
	));

	public static ItemIdentifier BLOCK_DESTROYER = add(new CustomItemIdentifier(
			Items.STRUCTURE_BLOCK, "ft:block-destroyer", "Блок дамагер", false,
			new LoreChecker("Каст:", "Нанесение урона", "Радиус:", "1,5 блока")
	));
}
