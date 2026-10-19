package com.perfect.client.gui.hud.elements.functions;

import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.gui.hud.common.AbstractElementsListHud;

public class FunctionsHud extends AbstractElementsListHud {

	private final FunctionsHudConfig config = new FunctionsHudConfig(this);

	public FunctionsHud() {
		super("functions-hud", "Functions", 0.95f, 0.95f, true);
	}

	@Override
	protected void updateElements() {
		entries.clear();

		boolean hasElements = false;

		for (Function f : Managers.FUNCTION.getAll()) {
			if (f.isEnabled() && config.getCategories().get(f.getCategory())) {
				entries.add(createEntry(f.getName(), f.getStatus()));
				hasElements = true;
			}
		}

		if (!hasElements && isHudEditorScreen()) {
			entries.add(createEntry("Attack Aura", ""));
			entries.add(createEntry("Auto Crystal", "Artik, 10.2, 19"));
			entries.add(createEntry("Speed", "Strafe"));
			entries.add(createEntry("Flight", ""));
		}
	}
}