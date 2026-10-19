package com.perfect.client.core.event.events.movement;

import com.perfect.client.core.event.system.Cancellable;
import lombok.Getter;

@Getter

public class MovementIterationEvent extends Cancellable {

	private int iterations = 1;

	public void setIterations(int val) {
		iterations = Math.max(iterations, val);
	}
}
