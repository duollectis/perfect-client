package com.perfect.client.core.event.system;

import com.perfect.client.PerfectClient;

public abstract class EventListener implements IEventListener {

	public EventListener() {
		PerfectClient.EVENT_MANAGER.subscribe(this);
	}
}
