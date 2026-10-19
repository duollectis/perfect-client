package com.perfect.client.core.functional.function.helper.handler;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.config.FunctionConfig;
import com.perfect.client.core.functional.function.helper.FunctionHelper;

public abstract class FunctionHandler<T extends Function, K extends FunctionConfig<?>> extends FunctionHelper<T, K> implements IHandler {

	public FunctionHandler(T function, K config) {
		super(function, config);
	}

	@Override
	public final void enable() {
		onEnable();
		PerfectClient.EVENT_MANAGER.subscribe(this);
	}

	@Override
	public final void disable() {
		PerfectClient.EVENT_MANAGER.unsubscribe(this);
		onDisable();
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

	public final void setEnabled(boolean enable) {
		if (enable) {
			enable();
		}
		else {
			disable();
		}
	}
}
