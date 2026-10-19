package com.perfect.client.core.utils.enums;

import com.perfect.client.core.utils.LoggerUtils;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;

import java.util.*;


public class EnumOrder<T extends Enum<?>> implements ISerializable<EnumOrder<T>> {

	private final T[] values;
	private final Map<T, Boolean> enabledStatus;
	private final List<T> priorityOrder;

	public EnumOrder(EnumOrder<T> other) {
		values = other.values;
		enabledStatus = createDisabledMap();
		priorityOrder = new ArrayList<>(other.priorityOrder);
	}

	public EnumOrder(T[] values) {
		this.values = values;
		enabledStatus = createDisabledMap();

		this.priorityOrder = new ArrayList<>(Arrays.asList(values));

		for (T enumValue : values) {
			enabledStatus.put(enumValue, false);
		}
	}

	public int getPriority(T value) {
		return priorityOrder.size() - getOrder(value);
	}

	public int getOrder(T value) {
		return priorityOrder.indexOf(value);
	}

	public void move(T value, int index) {
		int current = priorityOrder.indexOf(value);

		if (current != index) {
			Collections.swap(priorityOrder, index, current);
		}
	}

	public void toggle(T value) {
		if (isEnabled(value)) {
			disable(value);
		}
		else {
			enable(value);
		}
	}

	public void enable(T value) {
		enabledStatus.put(value, true);
	}

	public void disable(T value) {
		enabledStatus.put(value, false);
	}

	public boolean isEnabled(T value) {
		return enabledStatus.get(value);
	}

	public Queue<T> getQueue() {
		List<T> enabledValues = priorityOrder
				.stream()
				.filter(enabledStatus::get)
				.toList();
		return new LinkedList<>(enabledValues);
	}

	public void setPriorityOrder(List<T> order) {
		this.priorityOrder.clear();
		this.priorityOrder.addAll(order);

		for (T value : values) {
			if (!priorityOrder.contains(value)) {
				priorityOrder.add(value);
			}
		}
	}

	public void disableAll() {
		for (T enumValue : values) {
			enabledStatus.put(enumValue, false);
		}
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		JsonArray priorityOrderJson = new JsonArray();
		for (T value : priorityOrder) {
			priorityOrderJson.add(value.name());
		}
		json.add("priorityOrder", priorityOrderJson);

		JsonArray enabledStatusJson = new JsonArray();
		for (T value : values) {
			if (isEnabled(value)) {
				enabledStatusJson.add(value.name());
			}
		}
		json.add("enabledStatus", enabledStatusJson);

		return json;
	}

	@Override
	public EnumOrder<T> fromJson(JsonObject json) {
		try {
			JsonArray priorityOrderJson = json.getJsonArray("priorityOrder");

			List<T> priorityOrder = new ArrayList<>();
			for (JsonElement<?> element : priorityOrderJson) {
				EnumUtils.valueFromName(values, element.asString()).ifPresent(priorityOrder::add);
			}
			setPriorityOrder(priorityOrder);

			JsonArray enabledStatusJson = json.getJsonArray("enabledStatus");

			disableAll();

			for (JsonElement<?> element : enabledStatusJson) {
				EnumUtils.valueFromName(values, element.asString()).ifPresent(this::enable);
			}
		}
		catch (Exception e) {
			LoggerUtils.warning("Failed to load enum order.");
		}
		return this;
	}

	private HashMap<T, Boolean> createDisabledMap() {
		HashMap<T, Boolean> map = new HashMap<>();
		for (T value : values) {
			map.put(value, false);
		}
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof EnumOrder<?> enumOrder) {
			return Arrays.equals(values, enumOrder.values)
					&& enabledStatus.equals(enumOrder.enabledStatus)
					&& priorityOrder.equals(enumOrder.priorityOrder);
		}
		return false;
	}
}
