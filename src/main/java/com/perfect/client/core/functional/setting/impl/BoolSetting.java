package com.perfect.client.core.functional.setting.impl;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.bind.BindListener;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.INamedToggleable;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.core.utils.functions.ChatUtils;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.toggle.BoolWidget;

import java.util.function.Consumer;

public class BoolSetting extends Setting<Boolean> implements IBoolSetting, INamedToggleable {

	private final AbstractBindListener bindListener;

	private BoolSetting(
			String name,
			Boolean defaultValue,
			Consumer<Boolean> onChanged,
			IVisible visible
	) {
		super(name, defaultValue, onChanged, visible, null);

		bindListener = new BindListener() {
			@Override
			public boolean isLock() {
				return function != null && !function.isEnabled();
			}
		};

		Managers.BIND.add(bindListener);

		bindListener.setToggleable(this);
	}

	@Override
	public void resetImpl() {
		value = defaultValue;

		if (bindListener != null) {
			bindListener.setBind(Bind.NONE);
		}
	}

	@Override
	public void toggle() {
		set(!get());

		// Todo: сделать красивое сообщение
		// TODO: это я буду делать

		String message = this.getName() + ": " + (get() ? "true" : "false");

		if (function == null) {
			ChatUtils.sendMsg(message);
		}
		else {
			function.info(hashCode(), message);
		}
	}

	@Override
	public void enable() {
		set(true);
	}

	@Override
	public void disable() {
		set(false);
	}

	@Override
	public boolean isEnabled() {
		return get();
	}

	public boolean isVisibleAndActive() {
		return isVisible() && get();
	}

	@Override
	public boolean isValueValid(Boolean value) {
		return true;
	}

	@Override
	public boolean wasChanged() {
		return super.wasChanged() || (bindListener.getBind() != Bind.NONE);
	}

	@Override
	public void save(JsonObject json) {
		json.add("value", value);
		json.add("bind", bindListener.getBind().toJson());
	}

	@Override
	public void load(JsonObject json) {
		set(json.getBoolean("value", defaultValue));
		bindListener.setBind(Bind.fromJson(json.getJsonObject("bind")));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new BoolWidget(this);
	}

	@Override
	public AbstractBindListener getBindListener() {
		return bindListener;
	}

	@Override
	public String getToggleableId() {
		return (function == null ? "CLIENT-" : function.getName(false) + "-") + getName().replace(" ", "");
	}

	@Override
	public String getType() {
		return "setting";
	}

	@Override
	public void sendBoundMessage(String message) {
		if (function == null) {
			ChatUtils.sendMsg(0, message);
		}
		else {
			function.info(0, message);
		}
	}

	public static class Builder extends SettingBuilder<Builder, Boolean, BoolSetting> {

		public Builder() {
			super(false);
		}

		@Override
		public BoolSetting build() {
			return new BoolSetting(id, defaultValue, onChanged, visible);
		}
	}
}
