package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.core.utils.text.INameable;

public enum CombatVersion implements INameable {
	NEW("1.9+"),
	LEGACY("1.8");

	private final String name;

	CombatVersion(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}