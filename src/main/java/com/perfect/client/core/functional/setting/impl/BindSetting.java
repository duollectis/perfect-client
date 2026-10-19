package com.perfect.client.core.functional.setting.impl;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.bind.BindAction;
import com.perfect.client.core.functional.setting.Setting;
import com.perfect.client.core.functional.setting.interfaces.settings.IBindSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.IVisible;
import com.perfect.client.core.utils.files.json.system.JsonObject;
import com.perfect.client.gui.widgets.settings.SettingWidget;
import com.perfect.client.gui.widgets.settings.elements.BindWidget;

import java.util.function.Consumer;

public class BindSetting extends Setting<Bind> implements IBindSetting {

	private final AbstractBindListener bindListener;

	public BindSetting(
			String name,
			Bind defaultValue,
			Consumer<Bind> onChanged,
			IVisible visible,
			BoolSetting toggleable,
			Runnable action,
			BindAction bindAction,
			boolean enableOnScreens
	) {
		super(name, defaultValue == null ? Bind.NONE : defaultValue, onChanged, visible, toggleable);

		bindListener = new SettingBindListener(this);
		bindListener.setEnableOnScreens(enableOnScreens);

		if (action == null) {
			bindListener.setAction(bindAction);
		}
		else {
			bindListener.setAction((bind, event) -> {
				if (event.isPress()) {
					action.run();
				}
			});
		}
	}

	@Override
	public void resetImpl() {
		value = defaultValue;
	}

	@Override
	public boolean isValueValid(Bind value) {
		return true;
	}

	@Override
	public void save(JsonObject json) {
		json.add("value", value.toJson());
	}

	@Override
	public void load(JsonObject json) {
		bindListener.setBind(Bind.fromJson(json.getJsonObject("value")));
	}

	@Override
	public SettingWidget<?, ?> createWidget() {
		return new BindWidget(this);
	}

	@Override
	public AbstractBindListener getListener() {
		return bindListener;
	}

	public static class SettingBindListener extends AbstractBindListener {

		private final Setting<Bind> setting;

		public SettingBindListener(Setting<Bind> setting) {
			this.setting = setting;
		}

		@Override
		public boolean isLock() {
			return setting.getFunction() != null && !setting.getFunction().isEnabled();
		}

		@Override
		public Bind getBind() {
			return setting.get();
		}

		@Override
		public void setBind(Bind bind) {
			setting.set(bind);
		}
	}

	public static class Builder extends SettingBuilder<Builder, Bind, BindSetting> {

		private Runnable action;
		private BindAction bindAction;
		private boolean enableOnScreens;

		public Builder() {
			super(Bind.NONE);
		}

		public Builder action(Runnable action) {
			this.action = action;
			return this;
		}

		public Builder bindAction(BindAction bindAction) {
			this.bindAction = bindAction;
			return this;
		}

		public Builder enableOnScreens(boolean enableOnScreens) {
			this.enableOnScreens = enableOnScreens;
			return this;
		}

		@Override
		public BindSetting build() {
			BindSetting bindSetting = new BindSetting(
					id,
					defaultValue,
					onChanged,
					visible,
					toggleable,
					action,
					bindAction,
					enableOnScreens
			);

			Managers.BIND.add(bindSetting.bindListener);

			return bindSetting;
		}
	}
}
