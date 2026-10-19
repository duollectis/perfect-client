package com.perfect.client.core.functional.function.helper.container;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.functional.function.helper.handler.IHandler;

import java.util.HashMap;

public abstract class AbstractContainer<T, K extends FunctionHandler<?, ?>> implements IHandler {

	protected final Function function;

	protected final HashMap<T, K> handlers = new HashMap<>();

	protected AbstractContainer(Function function) {
		this.function = function;
	}

	@SuppressWarnings("unchecked")
	public <A extends FunctionHandler<?, ?>, B extends AbstractContainer<T, A>> B add(T key, K handler) {
		handlers.put(key, handler);
		return (B) this;
	}
}
