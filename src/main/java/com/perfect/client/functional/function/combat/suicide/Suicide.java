package com.perfect.client.functional.function.combat.suicide;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;

public class Suicide extends Function {

	private final SuicideConfig config = new SuicideConfig(this);

	public Suicide() {
		super("suicide", "Suicide", Category.COMBAT);
	}

	@Override
	public void onRespawn() {
		if (config.getToggleOnDeath().get()) {
			disable();
		}
	}
}
