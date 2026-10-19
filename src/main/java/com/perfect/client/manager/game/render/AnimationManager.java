package com.perfect.client.manager.game.render;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.render.animation.world.IWorldAnimation;

import java.util.ArrayList;
import java.util.List;

public class AnimationManager extends Manager {

	private final List<IWorldAnimation> scanAnimations = new ArrayList<>();

	@Override
	public void onPostTick() {
		if (scanAnimations.isEmpty()) {
			return;
		}

		synchronized (scanAnimations) {
			scanAnimations.removeIf(IWorldAnimation::update);
		}
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (scanAnimations.isEmpty()) {
			return;
		}

		synchronized (scanAnimations) {
			for (IWorldAnimation scanAnimation : scanAnimations) {
				scanAnimation.draw(event.getMatrices());
			}
		}
	}

	public void add(IWorldAnimation animation) {
		synchronized (scanAnimations) {
			scanAnimations.add(animation);
		}
	}
}