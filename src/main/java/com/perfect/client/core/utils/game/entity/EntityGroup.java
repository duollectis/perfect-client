package com.perfect.client.core.utils.game.entity;

import com.perfect.client.core.utils.text.ITranslatable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public enum EntityGroup implements ITranslatable {
	ANIMALS(EntityGroup::isAnimalsGroup),
	MONSTERS(EntityGroup::isMonstersGroup),
	OTHER(EntityGroup::isOtherGroup),
	PLAYERS(EntityGroup::isPlayersGroup),
	ITEMS(EntityGroup::isItemsGroup);

	private final Predicate<EntityType<?>> filter;

	EntityGroup(Predicate<EntityType<?>> filter) {
		this.filter = filter;
	}

	public boolean includes(EntityType<?> type) {
		return filter.test(type);
	}

	public List<EntityType<?>> getEntities(Predicate<EntityType<?>> filter) {
		List<EntityType<?>> list = new ArrayList<>();

		for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
			if (filter == null || filter.test(entityType)) {
				if (includes(entityType)) {
					list.add(entityType);
				}
			}
		}

		return list;
	}

	public boolean isVisible(Predicate<EntityType<?>> filter) {
		return !getEntities(filter).isEmpty();
	}

	public boolean isFullContains(Predicate<EntityType<?>> filter, Set<EntityType<?>> list) {
		for (EntityType<?> entityType : getEntities(filter)) {
			if (!list.contains(entityType)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isAnimalsGroup(EntityType<?> type) {
		return type.getSpawnGroup() == SpawnGroup.WATER_AMBIENT
				|| type.getSpawnGroup() == SpawnGroup.WATER_CREATURE
				|| type.getSpawnGroup() == SpawnGroup.UNDERGROUND_WATER_CREATURE
				|| type.getSpawnGroup() == SpawnGroup.AXOLOTLS
				|| type.getSpawnGroup() == SpawnGroup.CREATURE;
	}

	private static boolean isMonstersGroup(EntityType<?> type) {
		return type.getSpawnGroup() == SpawnGroup.MONSTER;
	}

	private static boolean isOtherGroup(EntityType<?> type) {
		if (type == EntityType.PLAYER) {
			return false;
		}
		return type.getSpawnGroup() == SpawnGroup.MISC || type.getSpawnGroup() == SpawnGroup.AMBIENT;
	}

	private static boolean isPlayersGroup(EntityType<?> type) {
		return type == EntityType.PLAYER;
	}

	private static boolean isItemsGroup(EntityType<?> type) {
		return type == EntityType.ITEM;
	}

	@Override
	public String getTranslationId() {
		return "entities";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}