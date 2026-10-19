package com.perfect.client.core.functional.function.helper.handler;

import com.perfect.client.core.event.system.IEventListener;

public interface IHandler extends IEventListener {

	default void enable() {
		onEnable();
	}

	default void disable() {
		onDisable();
	}

	void onEnable();
	void onDisable();
}
