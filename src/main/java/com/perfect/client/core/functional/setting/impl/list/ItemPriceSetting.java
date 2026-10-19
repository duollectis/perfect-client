package com.perfect.client.core.functional.setting.impl.list;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.IItemList;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.ItemPrice;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.list.ItemPriceWidget;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ItemPriceSetting extends Setting<Map<ItemIdentifier, ItemPrice>> implements ISetting<Map<ItemIdentifier, ItemPrice>>, IItemList<ItemIdentifier> {

	private final List<ItemIdentifier> availableItems;

	private ItemPriceSetting(
			String id,
			List<ItemIdentifier> availableItems,
			Map<ItemIdentifier, ItemPrice> defaultValue,
			Consumer<Map<ItemIdentifier, ItemPrice>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(id, defaultValue, onChanged, visible, toggleable);
		this.availableItems = availableItems;
	}

	public ItemPrice computeIfAbsent(ItemIdentifier item) {
		return value.computeIfAbsent(item, i -> new ItemPrice(item));
	}

	public ItemPrice get(ItemIdentifier item) {
		return value.getOrDefault(item, new ItemPrice(item));
	}

	@Override
	public void resetImpl() {
		value = new HashMap<>(defaultValue);
	}

	@Override
	public boolean isValueValid(Map<ItemIdentifier, ItemPrice> value) {
		return true;
	}

	@Override
	protected void save(JsonObject json) {
		JsonArray jsonArray = new JsonArray();

		for (Map.Entry<ItemIdentifier, ItemPrice> entry : value.entrySet()) {
			ItemIdentifier item = entry.getKey();
			ItemPrice price = entry.getValue();

			if (price.isSet()) {
				String itemId = item.getId();

				JsonObject jsonObject = new JsonObject();

				jsonObject.add("item", itemId);
				jsonObject.add("value", price.toJson());

				jsonArray.add(jsonObject);
			}
		}

		json.add("value", jsonArray);
	}

	@Override
	protected void load(JsonObject json) {
		Map<ItemIdentifier, ItemPrice> value = new HashMap<>();
		JsonArray jsonArray = json.getJsonArray("value");

		for (JsonElement<?> jsonElement : jsonArray) {
			JsonObject jsonObject = jsonElement.asJsonObject();

			String itemId = jsonObject.getString("item");

			if (itemId != null) {
				ItemIdentifier item = Managers.ITEM.getById(itemId);
				ItemPrice price = new ItemPrice(item).fromJson(jsonObject.getJsonObject("value"));

				if (availableItems.contains(item)) {
					value.put(item, price);
				}
			}
		}

		set(value);
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new ItemPriceWidget(this);
	}

	@Override
	public String getItemName(ItemIdentifier value) {
		return value.getName();
	}

	@Override
	public List<ItemIdentifier> getItemList() {
		return availableItems;
	}

	@Override
	public void toggle(ItemIdentifier item) {
		value.computeIfAbsent(item, k -> new ItemPrice(item)).toggle();
	}

	@Override
	public boolean contains(ItemIdentifier item) {
		return value.getOrDefault(item, new ItemPrice(item)).isEnabled();
	}

	public static class Builder extends SettingBuilder<Builder, Map<ItemIdentifier, ItemPrice>, ItemPriceSetting> {

		private final List<ItemIdentifier> availableItems = new ArrayList<>();

		public Builder() {
			super(new HashMap<>());
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
		public ItemPriceSetting build() {
			return new ItemPriceSetting(id, availableItems, defaultValue, onChanged, visible, toggleable);
		}
	}
}
