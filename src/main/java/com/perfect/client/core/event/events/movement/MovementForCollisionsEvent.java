package com.perfect.client.core.event.events.movement;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor

public class MovementForCollisionsEvent extends Cancellable {

	private Vec3d movement;

	public void setMovement(Vec3d movement) {
		this.movement = movement;
		cancel();
	}
}
