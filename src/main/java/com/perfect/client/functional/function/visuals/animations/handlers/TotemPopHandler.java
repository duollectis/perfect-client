package com.perfect.client.functional.function.visuals.animations.handlers;

import com.perfect.client.core.event.events.entity.PlayerEvent;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.functional.function.visuals.animations.Animations;
import com.perfect.client.functional.function.visuals.animations.AnimationsConfig;
import com.perfect.client.functional.function.visuals.animations.utils.AnimationsHandler;

public class TotemPopHandler extends AnimationsHandler {

	public TotemPopHandler(
			Animations function,
			AnimationsConfig config
	) {
		super(function, config);
	}

	@Override
	public void onPlayerTotemPop(PlayerEvent event) {
		if (!config.getTotemPopAnimation().get()) {
			return;
		}

		if (config.getIgnoreSelf().get() && event.getPlayer() == mc.player) {
			return;
		}

		addAnimation(
				event.getPlayer(),
				MultiColor.client(),
				MultiColor.clientFill(),
				9,
				config.getTotemPopAnimationTime().get(),
				true,
				config.getTotemPopWaveMode().get()
		);
	}
}
