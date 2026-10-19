package com.perfect.client.core.utils.functions.mining;

import com.perfect.client.core.utils.text.ITranslatable;

public enum BreakColor implements ITranslatable {
	PROGRESS,
	STATIC;

	@Override
	public String getTranslationId() {
		return "break-color";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
