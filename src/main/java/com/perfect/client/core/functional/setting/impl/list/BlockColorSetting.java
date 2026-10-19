package com.perfect.client.core.functional.setting.impl.list;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.utils.BlockColor;
import com.perfect.client.core.utils.IItemList;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.RegistryUtils;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.list.BlockColorWidget;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockColorSetting extends Setting<Map<Block, BlockColor>> implements ISetting<Map<Block, BlockColor>>, IItemList<Block> {

	private final Predicate<Block> filter;

	public BlockColorSetting(
			String id,
			Map<Block, BlockColor> defaultValue,
			Predicate<Block> filter,
			Consumer<Map<Block, BlockColor>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(id, defaultValue, onChanged, visible, toggleable);
		this.filter = filter;
	}

	@Override
	public String getItemName(Block value) {
		return StringUtils.textToString(value.getName());
	}

	@Override
	public List<Block> getItemList() {
		return RegistryUtils.getFiltered(Registries.BLOCK, filter);
	}

	@Override
	public void toggle(Block block) {
		value.computeIfAbsent(block, k -> new BlockColor()).toggleEnabled();
		onChanged();
	}

	@Override
	public boolean contains(Block block) {
		return get(block).isEnabled();
	}

	public void setColor(Block block, Color color) {
		value.computeIfAbsent(block, b -> new BlockColor()).setColor(color);
	}

	public void toggleSet(Block block) {
		BlockColor blockColor = value.computeIfAbsent(block, b -> new BlockColor());

		blockColor.toggleSet();

		if (!blockColor.isEnabled() && !blockColor.isSet()) {
			value.remove(block);
		}
	}

	public BlockColor get(Block block) {
		return value.getOrDefault(block, new BlockColor());
	}

	@Override
	public void resetImpl() {
		value = new HashMap<>(defaultValue);
	}

	@Override
	public boolean isValueValid(Map<Block, BlockColor> value) {
		return true;
	}

	@Override
	protected void save(JsonObject json) {
		JsonArray jsonArray = new JsonArray();

		for (Map.Entry<Block, BlockColor> entry : value.entrySet()) {
			Block block = entry.getKey();
			BlockColor blockColor = entry.getValue();

			String blockId = Registries.BLOCK.getId(block).toString();

			JsonObject jsonObject = new JsonObject();

			jsonObject.add("block", blockId);
			jsonObject.add("value", blockColor.toJson());

			jsonArray.add(jsonObject);
		}

		json.add("value", jsonArray);
	}

	@Override
	protected void load(JsonObject json) {
		Map<Block, BlockColor> value = new HashMap<>();
		JsonArray jsonArray = json.getJsonArray("value");

		for (JsonElement<?> jsonElement : jsonArray) {
			JsonObject jsonObject = jsonElement.asJsonObject();

			String blockId = jsonObject.getString("block");

			if (blockId != null) {
				Block block = Registries.BLOCK.get(Identifier.of(blockId));
				BlockColor blockColor = new BlockColor().fromJson(jsonObject.getJsonObject("value"));

				if (filter == null || filter.test(block)) {
					value.put(block, blockColor);
				}
			}
		}
		set(value);
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new BlockColorWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Map<Block, BlockColor>, BlockColorSetting> {

		private Predicate<Block> filter;

		public Builder() {
			super(new HashMap<>());
		}

		public Builder defaultValue(Block... defaults) {
			HashMap<Block, BlockColor> defaultValue = new HashMap<>();

			for (Block block : defaults) {
				defaultValue.put(block, new BlockColor().enabled());
			}

			return defaultValue(defaultValue);
		}

		public Builder filter(Predicate<Block> filter) {
			this.filter = filter;
			return this;
		}

		@Override
		public BlockColorSetting build() {
			return new BlockColorSetting(id, defaultValue, filter, onChanged, visible, toggleable);
		}
	}
}
