package com.perfect.client.gui.hud.elements;

import com.perfect.client.core.functional.bind.Bind;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.gui.hud.common.AbstractElementsListHud;

public class BindsHud extends AbstractElementsListHud {

	public BindsHud() {
		super("binds-hud", "Binds", 0.93f, 0.0f, true);
	}

	@Override
	protected void updateElements() {
		entries.clear();

		boolean hasElements = false;

		for (Function f : Managers.FUNCTION.getAll()) {
			Bind bind = f.getBindListener().getBind();

			if (f.isEnabled() && bind.isSet()) {
				entries.add(createEntry(f.getName(), bind.toString()));
				hasElements = true;
			}
		}

		if (!hasElements && isHudEditorScreen()) {
			entries.add(createEntry("Attack Aura", "R"));
			entries.add(createEntry("Auto Crystal", "C"));
			entries.add(createEntry("Speed", "U"));
			entries.add(createEntry("Flight", "Y"));
		}
	}
}