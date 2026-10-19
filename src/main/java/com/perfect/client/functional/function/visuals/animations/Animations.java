package com.perfect.client.functional.function.visuals.animations;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ToggleContainer;
import com.perfect.client.functional.function.visuals.animations.handlers.PlayerDeathHandler;
import com.perfect.client.functional.function.visuals.animations.handlers.TotemPopHandler;
import com.perfect.client.functional.function.visuals.animations.utils.AnimationsHandler;

public class Animations extends Function {

	private final AnimationsConfig config = new AnimationsConfig(this);

	private final ToggleContainer<AnimationsHandler> toggleContainer = createToggleHandler();

	public Animations() {
		super("animations", "Animations", Category.VISUALS);

		toggleContainer.add(config.getTotemPopAnimation(), new TotemPopHandler(this, config));
		toggleContainer.add(config.getDeathAnimation(), new PlayerDeathHandler(this, config));
	}

	public void onToggled() {
		toggleContainer.onToggled();
	}
}
