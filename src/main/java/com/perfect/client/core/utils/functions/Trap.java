package com.perfect.client.core.utils.functions;

import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.text.ITranslatable;
import lombok.Getter;

@Getter
public enum Trap implements ITranslatable {
	FULL(TrapType.TRAP),
	FACE(TrapType.FACE),
	HEAD(TrapType.HEAD);

	private final TrapType type;

	Trap(TrapType type) {
		this.type = type;
	}

	@Override
	public String getTranslationId() {
		return "trap";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
