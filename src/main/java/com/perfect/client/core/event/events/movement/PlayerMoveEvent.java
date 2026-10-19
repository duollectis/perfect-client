package com.perfect.client.core.event.events.movement;

import com.perfect.client.core.event.system.Cancellable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;

@Setter
@Getter
@AllArgsConstructor

public class PlayerMoveEvent extends Cancellable {

	private final MovementType type;
	private double x, y, z;

	public Vec3d getMovement() {
		return new Vec3d(x, y, z);
	}
}
