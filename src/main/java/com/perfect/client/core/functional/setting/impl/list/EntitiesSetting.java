package com.perfect.client.core.functional.setting.impl.list;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IListSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.RegistryUtils;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.game.entity.EntityGroup;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.list.EntitiesWidget;
import lombok.Getter;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntitiesSetting extends Setting<Set<EntityType<?>>> implements IListSetting<EntityType<?>> {

	private final Predicate<EntityType<?>> filter;
	private final Set<EntityGroup> defaultGroups;

	@Getter
	private Set<EntityGroup> activeGroups = new HashSet<>();

	@Getter
	private int activeSize;

	private EntitiesSetting(
			String name,
			Set<EntityType<?>> defaultValue,
			Set<EntityGroup> defaultGroups,
			Consumer<Set<EntityType<?>>> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			Predicate<EntityType<?>> filter
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.filter = filter;
		this.defaultGroups = defaultGroups;
	}

	private void updateActiveSize() {
		int size = 0;

		for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
			if (filter == null || filter.test(entityType)) {
				if (contains(entityType)) {
					size++;
				}
			}
		}

		activeSize = size;
	}

	public EntityGroup[] getVisibleGroups() {
		Set<EntityGroup> visibleGroups = new HashSet<>();

		for (EntityGroup group : EntityGroup.values()) {
			if (group.isVisible(filter)) {
				visibleGroups.add(group);
			}
		}

		return visibleGroups.toArray(EntityGroup[]::new);
	}

	@Override
	public String getItemName(EntityType<?> value) {
		return StringUtils.textToString(value.getName());
	}

	public void addActiveGroup(EntityGroup group) {
		activeGroups.add(group);

		value.addAll(group.getEntities(filter));

		onChanged();
	}

	public void removeActiveGroup(EntityGroup group) {
		activeGroups.remove(group);

		if (group.isFullContains(filter, value)) {
			group.getEntities(filter).forEach(value::remove);
		}

		onChanged();
	}

	@Override
	public void toggle(EntityType<?> entityType) {
		if (contains(entityType)) {
			activeGroups.removeIf(group -> group.includes(entityType));
			value.remove(entityType);
		}
		else {
			value.add(entityType);
			checkFilledGroups();
		}

		onChanged();
	}

	private void checkFilledGroups() {
		for (EntityGroup group : EntityGroup.values()) {
			if (!activeGroups.contains(group) && group.isFullContains(filter, value)) {
				activeGroups.add(group);
			}
		}
	}

	@Override
	public List<EntityType<?>> getItemList() {
		return RegistryUtils.getFiltered(Registries.ENTITY_TYPE, filter);
	}

	@Override
	public boolean contains(EntityType<?> type) {
		if (value.contains(type)) {
			return true;
		}

		for (EntityGroup group : activeGroups) {
			if (filter == null || filter.test(type)) {
				if (group.includes(type)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void resetImpl() {
		value = new LinkedHashSet<>(defaultValue);

		if (defaultGroups != null) {
			activeGroups = new LinkedHashSet<>(defaultGroups);
		}
	}

	@Override
	public void onChanged() {
		super.onChanged();
		updateActiveSize();
	}

	@Override
	public boolean wasChanged() {
		return !Objects.equals(value, defaultValue) || !activeGroups.equals(defaultGroups);
	}

	@Override
	public boolean isValueValid(Set<EntityType<?>> value) {
		return true;
	}

	@Override
	public void save(JsonObject json) {
		JsonArray groupJson = new JsonArray();
		for (EntityGroup group : activeGroups) {
			groupJson.add(group.name());
		}
		json.add("groups", groupJson);

		JsonArray valueJson = new JsonArray();
		for (EntityType<?> entityType : get()) {
			String entityId = Registries.ENTITY_TYPE.getId(entityType).toString();
			valueJson.add(entityId);
		}
		json.add("entities", valueJson);
	}

	@Override
	public void load(JsonObject json) {
		activeGroups.clear();
		value.clear();

		JsonArray groupJson = json.getJsonArray("groups");
		for (JsonElement<?> element : groupJson) {
			String string = element.asString();

			EntityGroup group;

			try {
				group = EntityGroup.valueOf(string);
			}
			catch (Exception exception) {
				continue;
			}

			activeGroups.add(group);
		}

		JsonArray valueJson = json.getJsonArray("entities");
		for (JsonElement<?> element : valueJson) {
			String entityId = element.asString();

			if (entityId != null) {
				EntityType<?> type = Registries.ENTITY_TYPE.get(Identifier.of(entityId));

				if (filter == null || filter.test(type)) {
					value.add(type);
				}
			}
		}

		onChanged();
		checkFilledGroups();
	}

	public List<EntityType<?>> getValue() {
		List<EntityType<?>> types = new ArrayList<>(get());

		for (EntityGroup group : activeGroups) {
			for (EntityType<?> entityType : Registries.ENTITY_TYPE) {
				if (filter != null && !filter.test(entityType)) {
					continue;
				}

				if (group.includes(entityType)) {
					types.add(entityType);
				}
			}
		}

		return types;
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new EntitiesWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Set<EntityType<?>>, EntitiesSetting> {

		private Predicate<EntityType<?>> filter;
		private Set<EntityGroup> defaultGroups = new LinkedHashSet<>();

		public Builder() {
			super(new LinkedHashSet<>());
		}

		public Builder defaultValue(EntityType<?>... defaults) {
			defaultValue = new LinkedHashSet<>(Arrays.asList(defaults));
			return this;
		}

		public Builder defaultGroups(EntityGroup... groups) {
			defaultGroups = new LinkedHashSet<>(Arrays.asList(groups));
			return this;
		}

		public Builder filter(Predicate<EntityType<?>> filter) {
			this.filter = filter;
			return this;
		}

		public Builder onlyAttackable() {
			filter = EntityUtils::isAttackable;
			return this;
		}

		@Override
		public EntitiesSetting build() {
			return new EntitiesSetting(id, defaultValue, defaultGroups, onChanged, visible, toggleable, filter);
		}
	}
}
