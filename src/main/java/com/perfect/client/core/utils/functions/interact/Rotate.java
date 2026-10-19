package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.core.utils.text.ITranslatable;

public enum Rotate implements ITranslatable {
	NONE,
	VANILLA,
	PACKET,
	GRIM;

	@Override
	public String getTranslationId() {
		return "rotate";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
