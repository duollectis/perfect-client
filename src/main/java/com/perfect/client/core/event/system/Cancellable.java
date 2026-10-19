package com.perfect.client.core.event.system;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Cancellable {

	private boolean cancelled;
	private boolean breakEvents;

	public void cancel() {
		cancel(true);
	}

	public void cancel(boolean breakEvents) {
		setCancelled(true);
		setBreakEvents(breakEvents);
	}
}
