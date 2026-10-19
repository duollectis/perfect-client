package com.perfect.client.core.functional.function.helper.container;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SetModeContainer<T extends Enum<?>, K extends FunctionHandler<?, ?>> extends AbstractContainer<T, K> {

	private final Supplier<Set<T>> modeSupplier;

	@Getter
	private final Set<K> activeModes = new HashSet<>();

	public SetModeContainer(Supplier<Set<T>> modeSupplier, Function function) {
		super(function);

		this.modeSupplier = modeSupplier;
	}

	@Override
	public void onEnable() {
		set(modeSupplier.get());
	}

	@Override
	public void onDisable() {
		disableAll();
	}

	private void disableAll() {
		for (K handler : activeModes) {
			handler.disable();
		}

		activeModes.clear();
	}

	public void set(Set<T> modes) {
		disableAll();

		for (T mode : modes) {
			K handler = handlers.get(mode);

			if (handler != null) {
				activeModes.add(handler);

				if (function.isEnabled()) {
					handler.enable();
				}
			}
		}
	}
}