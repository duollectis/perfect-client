package com.perfect.client.functional.function.movement.speed.modes;

import com.perfect.client.core.event.events.movement.TravelEvent;
import com.perfect.client.functional.function.movement.speed.Speed;
import com.perfect.client.functional.function.movement.speed.SpeedConfig;
import com.perfect.client.functional.function.movement.speed.utils.SpeedMode;
import net.minecraft.util.math.Vec3d;


public class VanillaMode extends SpeedMode {

	private double xBoost, zBoost;

	public VanillaMode(Speed function, SpeedConfig config) {
		super(function, config);
	}

	@Override
	public void onPreTravel(TravelEvent event) {
		if (mc.player == null) {
			return;
		}

		Vec3d velocity = mc.player.getVelocity();

		double xAdd = (velocity.getX() > 0 && xBoost > 0) || (velocity.getX() < 0 && xBoost < 0) ? xBoost : 0;
		double zAdd = (velocity.getZ() > 0 && zBoost > 0) || (velocity.getZ() < 0 && zBoost < 0) ? zBoost : 0;

		mc.player.addVelocity(xAdd, 0, zAdd);

		xBoost = 0;
		zBoost = 0;
	}
}
