package com.perfect.client.core.event.system;

import com.perfect.client.PerfectClient;

public abstract class EventHook implements IEventListener {

	public void unhook() {
		PerfectClient.EVENT_MANAGER.unsubscribe(this);
	}
}
