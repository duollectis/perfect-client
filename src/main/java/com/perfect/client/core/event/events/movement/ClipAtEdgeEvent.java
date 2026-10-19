package com.perfect.client.core.event.events.movement;

import com.perfect.client.core.event.system.Cancellable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ClipAtEdgeEvent extends Cancellable {

	private boolean clip;
}
