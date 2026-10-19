package com.perfect.client.gui.widgets.list.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListItem {

	private final String name;

	private boolean enabled;

	private final Runnable toggleAction;
	private final Runnable pickAction;

	public void onToggle() {
		if (toggleAction != null) {
			toggleAction.run();
		}
	}

	public void onPick() {
		if (pickAction != null) {
			pickAction.run();
		}
	}
}
