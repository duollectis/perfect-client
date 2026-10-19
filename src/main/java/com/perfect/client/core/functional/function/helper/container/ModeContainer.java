package com.perfect.client.core.functional.function.helper.container;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import lombok.Getter;

import java.util.function.Supplier;

public class ModeContainer<T extends Enum<?>, K extends FunctionHandler<?, ?>> extends AbstractContainer<T, K> {

	private final Supplier<T> modeSupplier;

	@Getter
	private K currentMode;

	public ModeContainer(Supplier<T> modeSupplier, Function function) {
		super(function);
		this.modeSupplier = modeSupplier;
	}

	@Override
	public void onEnable() {
		set(modeSupplier.get());
	}

	@Override
	public void onDisable() {
		if (currentMode != null) {
			currentMode.disable();
		}
	}

	public void set(T mode) {
		if (currentMode != null) {
			currentMode.disable();
		}

		currentMode = getModeImpl(mode);

		if (function.isEnabled()) {
			currentMode.enable();
		}
	}

	private K getModeImpl(T mode) {
		return handlers.get(mode);
	}
}
