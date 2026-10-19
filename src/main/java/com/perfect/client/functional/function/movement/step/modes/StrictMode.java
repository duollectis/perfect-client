package com.perfect.client.functional.function.movement.step.modes;

import com.perfect.client.core.event.events.movement.MovementForCollisionsEvent;
import com.perfect.client.functional.function.movement.step.Step;
import com.perfect.client.functional.function.movement.step.StepConfig;
import com.perfect.client.functional.function.movement.step.utils.StepMode;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.step;


public class StrictMode extends StepMode {

	public StrictMode(
			Step function,
			StepConfig config
	) {
		super(function, config);
	}

	
	@Override
	public void onMovementForCollisions(MovementForCollisionsEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		Vec3d movement = event.getMovement();

		boolean active = step.isEnabled() && canStep(true);

		Box box = mc.player.getBoundingBox();
		List<VoxelShape> list = mc.world.getEntityCollisions(mc.player, box.stretch(movement));

		Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(
				mc.player,
				movement,
				box,
				mc.world,
				list
		);

		boolean bl = movement.x != vec3d.x;
		boolean bl2 = movement.y != vec3d.y;
		boolean bl3 = movement.z != vec3d.z;
		boolean bl4 = mc.player.isOnGround() || (!active && bl2 && movement.y < 0.0);

		double height = config.getHeight().get();

		if ((active ? height : mc.player.getStepHeight()) > 0.0f && bl4 && (bl || bl3)) {
			Vec3d vec3d2 = Entity.adjustMovementForCollisions(
					mc.player,
					new Vec3d(movement.x, active ? height : mc.player.getStepHeight(), movement.z),
					box,
					mc.world,
					list
			);

			Vec3d vec3d3 = Entity.adjustMovementForCollisions(
					mc.player,
					new Vec3d(0.0, active ? height : mc.player.getStepHeight(), 0.0),
					box.stretch(movement.x, 0.0, movement.z),
					mc.world,
					list
			);

			if (vec3d3.y < (active ? height : mc.player.getStepHeight())) {
				Vec3d vec3d4 = Entity.adjustMovementForCollisions(
						mc.player,
						new Vec3d(movement.x, 0.0, movement.z),
						box.offset(vec3d3),
						mc.world,
						list
				).add(vec3d3);

				if (vec3d4.horizontalLengthSquared() > vec3d2.horizontalLengthSquared()) {
					vec3d2 = vec3d4;
				}
			}

			if (vec3d2.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
				Vec3d v = vec3d2.add(Entity.adjustMovementForCollisions(
						mc.player,
						new Vec3d(0.0, -vec3d2.y + movement.y, 0.0),
						box.offset(vec3d2),
						mc.world,
						list
				));

				if (active) {
					step(getOffsets(v.y));
				}

				event.setMovement(v);
			}
		}
	}

	private double[] getOffsets(double step) {
		if (config.getMode().get() == Step.Mode.STRICT && step > 0.6 && step <= 1.000001)
			return new double[]{0.424, 0.33712, 0.25197759999999997};
		return null;
	}

	private void step(double[] offsets) {
		if (offsets == null || mc.getNetworkHandler() == null || mc.player == null)
			return;

		double offset = 0;
		for (double v : offsets) {
			offset += v;
			sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
					mc.player.getX(),
					mc.player.getY() + offset,
					mc.player.getZ(),
					false,
					mc.player.horizontalCollision
			));
		}
	}
}
