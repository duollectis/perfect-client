package com.perfect.client.functional.function.movement.elytraFly.modes;

import com.perfect.client.core.event.events.movement.PlayerMoveEvent;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.functional.function.movement.elytraFly.ElytraFly;
import com.perfect.client.functional.function.movement.elytraFly.ElytraFlyConfig;
import com.perfect.client.functional.function.movement.elytraFly.utils.FlyMode;
import lombok.Getter;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2d;

import static com.perfect.client.core.utils.functions.MovementUtils.forwardWithoutStrafe;

@Getter

public class BoostMode extends FlyMode {

	private boolean stopped;

	public BoostMode(ElytraFly function, ElytraFlyConfig config) {
		super(function, config);
	}

	
	@Override
	public void onPrePlayerMove(PlayerMoveEvent event) {
		stopped = false;

		if (mc.player == null) {
			return;
		}

		if (!PlayerUtils.isElytraFlying()) {
			return;
		}

		if (config.getStopBind().get().isKeyPressed()) {
			mc.player.setVelocity(0, -config.getFallSpeed().fromBPS(), 0);
			stopped = true;
			return;
		}

		if (config.getBoostBind().get().isSet() && !function.isBoostBindActive()) {
			return;
		}

		double normalizedLimit = config.getSpeedLimit().fromBPS();
		double boost = config.getBoost().get() / 100.0;

		if (config.getNcpBypass().get()) {
			Vector2d movement = forwardWithoutStrafe(boost);
			event.setX(event.getX() + movement.x);
			event.setZ(event.getZ() + movement.y);

			double xVel = event.getX();
			double yVel = event.getY();
			double zVel = event.getZ();

			double speed = Math.sqrt(xVel * xVel + yVel * yVel + zVel * zVel);

			if (speed > normalizedLimit) {
				double multiplier = normalizedLimit / speed;

				event.setX(event.getX() * multiplier);
				event.setZ(event.getZ() * multiplier);
			}
		}
		else {
			Vec3d movement = Vec3d.fromPolar(mc.player.getPitch(), mc.player.getYaw());

			if (normalizedLimit * normalizedLimit > mc.player.getVelocity().lengthSquared()) {
				event.setX(event.getX() + movement.x * boost);
				event.setY(event.getY() + movement.y * boost);
				event.setZ(event.getZ() + movement.z * boost);
			}
			else {
				event.setX(normalizedLimit * movement.x);
				event.setY(normalizedLimit * movement.y);
				event.setZ(normalizedLimit * movement.z);
			}
		}

		mc.player.setVelocity(event.getX(), event.getY(), event.getZ());
	}
}
