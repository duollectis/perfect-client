package com.perfect.client.core.functional.function.helper.container;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.utils.IToggleable;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class ToggleContainer<T extends FunctionHandler<?, ?>> extends AbstractContainer<IToggleable, T> {

	private final Set<T> activeHandlers = new HashSet<>();

	public ToggleContainer(Function function) {
		super(function);
	}

	@Override
	public void onEnable() {
		onToggled();
	}

	@Override
	public void onDisable() {
		handlers.values().forEach(handler -> handler.disable());
		activeHandlers.clear();
	}

	public void onToggled() {
		for (Map.Entry<IToggleable, T> entry : handlers.entrySet()) {
			onToggled(entry.getKey(), entry.getValue());
		}
	}

	private void onToggled(IToggleable toggleable, T handler) {
		if (!function.isEnabled()) {
			return;
		}

		boolean enabled = toggleable.isEnabled();
		handler.setEnabled(enabled);

		if (enabled) {
			activeHandlers.add(handler);
		}
		else {
			activeHandlers.remove(handler);
		}
	}
}
