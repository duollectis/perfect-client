package com.perfect.client.core.event.events.input.mouse;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class MouseMoveEvent extends Cancellable {

	private final float x;
	private final float y;
}
