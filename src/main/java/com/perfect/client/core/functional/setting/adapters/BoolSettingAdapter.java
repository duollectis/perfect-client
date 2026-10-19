package com.perfect.client.core.functional.setting.adapters;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.setting.interfaces.settings.IBoolSetting;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
public class BoolSettingAdapter extends SettingAdapter<Boolean> implements IBoolSetting {

	private Supplier<AbstractBindListener> bindListenerSupplier;

	@Override
	public AbstractBindListener getBindListener() {
		if (bindListenerSupplier == null) {
			return null;
		}

		return bindListenerSupplier.get();
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getFunctionId() {
		return null;
	}

	@Override
	public String getHudWidgetId() {
		return null;
	}
}
