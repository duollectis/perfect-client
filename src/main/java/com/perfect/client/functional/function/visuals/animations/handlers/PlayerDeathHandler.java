package com.perfect.client.functional.function.visuals.animations.handlers;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.functional.function.visuals.animations.Animations;
import com.perfect.client.functional.function.visuals.animations.AnimationsConfig;
import com.perfect.client.functional.function.visuals.animations.utils.AnimationsHandler;

public class PlayerDeathHandler extends AnimationsHandler {

	public PlayerDeathHandler(
			Animations function,
			AnimationsConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPlayerDeath(PlayerEvent event) {
		if (!config.getDeathAnimation().get()) {
			return;
		}

		addAnimation(
				event.getPlayer(),
				config.getDeathAnimationLineColor().get(),
				config.getDeathAnimationSideColor().get(),
				13,
				config.getDeathAnimationTime().get(),
				false,
				config.getDeathWaveMode().get()
		);
	}
}
