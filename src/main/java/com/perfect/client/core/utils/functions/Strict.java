package com.perfect.client.core.utils.functions;

import com.perfect.client.core.utils.text.ITranslatable;

public enum Strict implements ITranslatable {
	NONE,
	GRIM,
	FULL;

	public boolean isStrict() {
		return this == GRIM || this == FULL;
	}

	@Override
	public String getTranslationId() {
		return "strict";
	}

	@Override
	public boolean isGlobal() {
		return false;
	}
}
