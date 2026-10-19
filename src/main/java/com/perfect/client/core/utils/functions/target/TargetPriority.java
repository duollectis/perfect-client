package com.perfect.client.core.utils.functions.target;

import com.perfect.client.core.utils.text.ITranslatable;

public enum TargetPriority implements ITranslatable {
	HEALTH,
	RANGE,
	FIELD_OF_VIEW;

	@Override
	public String getTranslationId() {
		return "target-priority";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
