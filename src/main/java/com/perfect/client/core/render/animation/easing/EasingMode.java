package com.perfect.client.core.render.animation.easing;

import com.perfect.client.core.utils.text.ITranslatable;

public enum EasingMode implements ITranslatable {
	LINEAR(Easing.LINEAR),
	CUBIC(Easing.IN_OUT_CUBIC),
	DOUBLE(Easing.IN_OUT_DOUBLE);

	private final IEasing easing;

	EasingMode(IEasing easing) {
		this.easing = easing;
	}

	public IEasing impl() {
		return easing;
	}

	@Override
	public String getTranslationId() {
		return "easing-mode";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}
