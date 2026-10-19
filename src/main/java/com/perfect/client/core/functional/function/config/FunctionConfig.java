package com.perfect.client.core.functional.function.config;

import com.perfect.client.core.functional.function.Function;
import lombok.Getter;

@Getter
public abstract class FunctionConfig<T extends Function> extends Config {

	protected final T function;

	public FunctionConfig(T function) {
		super(function.getSettings());
		this.function = function;
	}
}
