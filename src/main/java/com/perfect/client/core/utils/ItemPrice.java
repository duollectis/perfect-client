package com.perfect.client.core.utils;

import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.itemIdentifier.ItemIdentifier;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemPrice implements ISerializable<ItemPrice> {

	private final int maxCount;

	private boolean enabled;

	private int buyPrice;
	private int sellPrice;
	private int sellCount;

	public ItemPrice(ItemIdentifier item) {
		maxCount = item.getDefaultStack().getMaxCount();
		sellCount = maxCount;
	}

	public boolean isSet() {
		return enabled || buyPrice != 0 || sellPrice != 0 || sellCount != maxCount;
	}

	public void replaceInvalidValues() {
		buyPrice = getValidValue(buyPrice, 0);
		sellPrice = getValidValue(sellPrice, 0);
		sellCount = getValidValue(sellCount, maxCount);

		sellCount = Math.min(sellCount, maxCount);
	}

	private int getValidValue(int val, int defaultValue) {
		if (val > 0) {
			return val;
		}
		return defaultValue;
	}

	public void toggle() {
		enabled = !enabled;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("enabled", enabled);
		json.add("buy_price", buyPrice);
		json.add("sell_price", sellPrice);
		json.add("sell_count", sellCount);

		return json;
	}

	@Override
	public ItemPrice fromJson(JsonObject json) {
		enabled = json.getBoolean("enabled");

		buyPrice = json.getInt("buy_price");
		sellPrice = json.getInt("sell_price");
		sellCount = json.getInt("sell_count", maxCount);

		return this;
	}
}
