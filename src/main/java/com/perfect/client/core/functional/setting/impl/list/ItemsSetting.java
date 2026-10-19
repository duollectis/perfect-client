package com.perfect.client.core.functional.setting.impl.list;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IListSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.list.ItemsWidget;
import net.minecraft.item.Item;

import java.util.*;
import java.util.function.Consumer;

public class ItemsSetting extends Setting<Set<ItemIdentifier>> implements IListSetting<ItemIdentifier> {

	private final List<ItemIdentifier> availableItems;

	private ItemsSetting(
			String name,
			List<ItemIdentifier> availableItems,
			Set<ItemIdentifier> defaultValue,
			Consumer<Set<ItemIdentifier>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);
		this.availableItems = availableItems;
	}

	@Override
	public String getItemName(ItemIdentifier value) {
		return value.getName();
	}

	@Override
	public void toggle(ItemIdentifier item) {
		if (!value.contains(item)) {
			value.add(item);
		}
		else {
			value.remove(item);
		}
		onChanged();
	}

	@Override
	public List<ItemIdentifier> getItemList() {
		return availableItems;
	}

	@Override
	public boolean contains(ItemIdentifier val) {
		return value.contains(val);
	}

	@Override
	public void resetImpl() {
		value = new LinkedHashSet<>(defaultValue);
	}

	@Override
	public boolean isValueValid(Set<ItemIdentifier> value) {
		return true;
	}

	@Override
	protected void save(JsonObject json) {
		JsonArray valueJson = new JsonArray();

		for (ItemIdentifier item : get()) {
			String itemId = item.getId();
			valueJson.add(itemId);
		}

		json.add("value", valueJson);
	}

	@Override
	protected void load(JsonObject json) {
		Set<ItemIdentifier> value = new LinkedHashSet<>();
		JsonArray valueJson = json.getJsonArray("value");

		for (JsonElement<?> element : valueJson) {
			String itemId = element.asString();

			if (itemId != null) {
				ItemIdentifier item = Managers.ITEM.getById(itemId);

				if (availableItems.contains(item)) {
					value.add(item);
				}
			}
		}
		set(value);
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new ItemsWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Set<ItemIdentifier>, ItemsSetting> {

		private final List<ItemIdentifier> availableItems = new ArrayList<>();

		public Builder() {
			super(new LinkedHashSet<>());
		}

		public Builder defaultValue(ItemIdentifier... defaults) {
			defaultValue = new LinkedHashSet<>(Arrays.asList(defaults));
			return this;
		}

		public Builder availableItems(Item... items) {
			for (Item item : items) {
				availableItems.add(Managers.ITEM.identifierOf(item));
			}
			return this;
		}

		public Builder availableItems(List<ItemIdentifier> items) {
			availableItems.addAll(items);
			return this;
		}

		@Override
		public ItemsSetting build() {
			return new ItemsSetting(id, availableItems, defaultValue, onChanged, visible, toggleable);
		}
	}
}
