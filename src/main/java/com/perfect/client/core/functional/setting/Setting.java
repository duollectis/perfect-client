package com.perfect.client.core.functional.setting;

import com.perfect.client.core.functional.setting.impl.BoolSetting;
import com.perfect.client.core.functional.setting.interfaces.ISetting;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import com.perfect.client.core.utils.IUpdatable;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.ISerializable;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Setting<T> extends BaseSetting implements ISetting<T>, ISerializable<T> {

	private final Consumer<T> onChanged;

	protected final T defaultValue;

	private final BoolSetting toggleable;

	protected T value;

	public Setting(
			String id,
			T defaultValue,
			Consumer<T> onChanged,
			IVisible visible,
			BoolSetting toggleable
	) {
		super(id, visible);
		this.defaultValue = defaultValue;
		this.onChanged = onChanged;
		this.toggleable = toggleable;

		resetImpl();
	}

	@Override
	public IBoolSetting getToggleable() {
		return toggleable;
	}

	public boolean isEnabled() {
		return toggleable == null || toggleable.get();
	}

	@Override
	public T get() {
		return value;
	}

	@Override
	public T getDefaultValue() {
		return defaultValue;
	}

	@Override
	public boolean set(T value) {
		if (!isValueValid(value)) {
			return false;
		}

		this.value = value;
		onChanged();
		return true;
	}

	@Override
	public void reset() {
		resetImpl();
		onChanged();

		if (toggleable != null) {
			toggleable.reset();
		}
	}

	protected void resetImpl() {
		value = defaultValue;
	}

	public boolean wasChanged() {
		return !Objects.equals(value, defaultValue);
	}

	public void onChanged() {
		if (onChanged != null) {
			onChanged.accept(value);
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();

		json.add("name", id);

		if (toggleable != null && toggleable.wasChanged()) {
			JsonObject toggleableJson = new JsonObject();
			toggleable.save(toggleableJson);
			json.add("toggleable", toggleableJson);
		}

		save(json);

		return json;
	}

	public T fromJson(JsonObject json) {
		if (toggleable != null) {
			JsonObject toggleableJson = json.getJsonObject("toggleable");

			if (toggleableJson != null) {
				toggleable.load(toggleableJson);
			}
		}

		load(json);

		T value = get();
		onChanged();

		return value;
	}

	@Override
	public String toString() {
		if (function != null) {
			return function.getId() + ":" + id;
		}

		return id;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public abstract boolean isValueValid(T value);

	protected abstract void save(JsonObject json);

	protected abstract void load(JsonObject json);

	@Override
	public abstract SettingWidget<?, ?> createWidget();

	@SuppressWarnings("unchecked")
	public abstract static class SettingBuilder<B, V, S> {

		protected String id = "";
		protected V defaultValue;
		protected IVisible visible;
		protected Consumer<V> onChanged;
		protected BoolSetting toggleable;

		protected SettingBuilder(V defaultValue) {
			this.defaultValue = defaultValue;
		}

		public B id(String id) {
			this.id = id;
			return (B) this;
		}

		public B defaultValue(V defaultValue) {
			this.defaultValue = defaultValue;
			return (B) this;
		}

		public B visible(IVisible visible) {
			this.visible = visible;
			return (B) this;
		}

		public B onChanged(Consumer<V> onChanged) {
			this.onChanged = onChanged;
			return (B) this;
		}

		public B onChanged(Runnable onChanged) {
			this.onChanged = val -> onChanged.run();
			return (B) this;
		}

		public B onChanged(Supplier<IUpdatable> onChanged) {
			this.onChanged = val -> onChanged.get().update();
			return (B) this;
		}

		public B toggleable(boolean defaultEnabled) {
			toggleable = new BoolSetting
					.Builder()
					.defaultValue(defaultEnabled)
					.build();
			return (B) this;
		}

		public abstract S build();
	}
}
