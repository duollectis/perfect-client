package com.perfect.client.functional.function.movement.safeWalk.modes;

import com.perfect.client.core.event.events.movement.ClipAtEdgeEvent;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalk;
import com.perfect.client.functional.function.movement.safeWalk.SafeWalkConfig;
import com.perfect.client.functional.function.movement.safeWalk.utils.SafeWalkMode;


public class NormalMode extends SafeWalkMode {

	public NormalMode(
			SafeWalk function,
			SafeWalkConfig config
	) {
		super(function, config);
	}

	@Override
	public void onClipAtEdge(ClipAtEdgeEvent event) {
		event.setClip(true);
		event.cancel();
	}
}
