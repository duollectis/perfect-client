package com.perfect.client.functional.function.movement.waterSpeed;

import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import org.joml.Vector2d;

import static com.perfect.client.core.utils.functions.MovementUtils.forwardWithoutStrafe;


public class WaterSpeed extends Function {

	private final WaterSpeedConfig config = new WaterSpeedConfig(this);

	public WaterSpeed() {
		super("water-speed", "Water Speed", Category.MOVEMENT);
	}

	
	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		if (mc.player == null || !mc.player.isSwimming())
			return;

		Vector2d movement = forwardWithoutStrafe(config.getBoost().get() / 100.0);
		event.setX(event.getX() + movement.x);
		event.setZ(event.getZ() + movement.y);

		double xVel = event.getX();
		double yVel = event.getY();
		double zVel = event.getZ();

		double speed = Math.sqrt(xVel * xVel + yVel * yVel + zVel * zVel);
		double limit = config.getSpeedLimit().fromBPS();

		if (speed > limit) {
			event.setX(event.getX() * limit / speed);
			event.setZ(event.getZ() * limit / speed);
		}

		mc.player.setVelocity(event.getX(), event.getY(), event.getZ());
	}
}
