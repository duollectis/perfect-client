package com.perfect.client.core.functional.setting.adapters;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.setting.interfaces.settings.IBindSetting;
import lombok.Setter;

import java.util.function.Supplier;

@Setter
public class BindSettingAdapter extends SettingAdapter<Bind> implements IBindSetting {

	private Supplier<AbstractBindListener> bindListenerSupplier;

	@Override
	public AbstractBindListener getListener() {
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
