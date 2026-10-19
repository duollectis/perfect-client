package com.perfect.client.core.functional.function;

import com.perfect.client.core.utils.text.INameable;

public enum Category implements INameable {
	COMBAT("Combat"),
	MOVEMENT("Movement"),
	PLAYER("Player"),
	VISUALS("Visuals"),
	MISC("Misc");

	private final String name;

	Category(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
