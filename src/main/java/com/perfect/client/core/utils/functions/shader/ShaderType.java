package com.perfect.client.core.utils.functions.shader;

import com.perfect.client.core.utils.text.ITranslatable;


public enum ShaderType implements ITranslatable {
	DEFAULT,
	WAVE,
	GRADIENT;

	@Override
	public String getTranslationId() {
		return "shader-type";
	}

	@Override
	public boolean isGlobal() {
		return true;
	}
}