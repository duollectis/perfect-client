package com.perfect.client.core.event.events.movement;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;

@Getter
@AllArgsConstructor

public class UpdateVelocityEvent extends Cancellable {

	private final Vec3d movementInput;
	private final float speed;
	private final float yaw;
}
