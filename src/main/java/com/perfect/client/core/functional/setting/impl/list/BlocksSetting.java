package com.perfect.client.core.functional.setting.impl.list;

import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IListSetting;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.RegistryUtils;
import com.perfect.client.core.utils.files.json.system.JsonArray;
import com.perfect.client.core.utils.files.json.system.JsonElement;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.text.StringUtils;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.list.BlocksWidget;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlocksSetting extends Setting<Set<Block>> implements IListSetting<Block> {

	private final Predicate<Block> filter;

	private BlocksSetting(
			String name,
			Set<Block> defaultValue,
			Predicate<Block> filter,
			Consumer<Set<Block>> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(name, defaultValue, onChanged, visible, toggleable);

		this.filter = filter;
	}

	@Override
	public String getItemName(Block value) {
		return StringUtils.textToString(value.getName());
	}

	@Override
	public void toggle(Block block) {
		if (!value.contains(block)) {
			value.add(block);
		}
		else {
			value.remove(block);
		}
		onChanged();
	}

	@Override
	public List<Block> getItemList() {
		return RegistryUtils.getFiltered(Registries.BLOCK, filter);
	}

	@Override
	public boolean contains(Block val) {
		return value.contains(val);
	}

	@Override
	public void resetImpl() {
		value = new LinkedHashSet<>(defaultValue);
	}

	@Override
	public boolean isValueValid(Set<Block> value) {
		return true;
	}

	@Override
	protected void save(JsonObject json) {
		JsonArray valueJson = new JsonArray();

		for (Block block : get()) {
			String blockId = Registries.BLOCK.getId(block).toString();
			valueJson.add(blockId);
		}

		json.add("value", valueJson);
	}

	@Override
	protected void load(JsonObject json) {
		Set<Block> value = new LinkedHashSet<>();
		JsonArray valueJson = json.getJsonArray("value");

		for (JsonElement<?> element : valueJson) {
			String blockId = element.asString();

			if (blockId != null) {
				Block block = Registries.BLOCK.get(Identifier.of(blockId));

				if (filter == null || filter.test(block)) {
					value.add(block);
				}
			}
		}
		set(value);
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new BlocksWidget(this);
	}

	public static class Builder extends SettingBuilder<Builder, Set<Block>, BlocksSetting> {

		private Predicate<Block> filter;

		public Builder() {
			super(new LinkedHashSet<>());
		}

		public Builder defaultValue(Block... defaults) {
			defaultValue = new LinkedHashSet<>(Arrays.asList(defaults));
			return this;
		}

		public Builder filter(Block... blocks) {
			Set<Block> filteredBlocks = new HashSet<>(Arrays.asList(blocks));
			this.filter = filteredBlocks::contains;
			return this;
		}

		public Builder filter(Predicate<Block> filter) {
			this.filter = filter;
			return this;
		}

		@Override
		public BlocksSetting build() {
			return new BlocksSetting(id, defaultValue, filter, onChanged, visible, toggleable);
		}
	}
}
