package com.perfect.client.manager.game.rotation;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.movement.UpdateVelocityEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.functions.MovementUtils;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.game.rotation.Angle;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.core.utils.game.rotation.RotationUtils.getPitch;
import static com.perfect.client.core.utils.game.rotation.RotationUtils.getYaw;

public class RotationManager extends Manager {

	@Getter
	private float sentYaw, sentPitch;

	@Getter
	private float sentYawDelta, sentPitchDelta;

	@Setter
	@Getter
	private float rotateYaw, rotatePitch;
	private float lastRotateYaw, lastRotatePitch;

	private float yaw;
	private float pitch;
	private float movementYaw;
	private float prevYaw;

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.player == null) {
			return;
		}
		if (clientConfig.getClientRotation()) {
			if (!Float.isNaN(lastRotateYaw)) {
				mc.player.setYaw(MathHelper.lerpAngleDegrees(RenderUtils.getTickDelta(), yaw, lastRotateYaw));
			}
			if (!Float.isNaN(lastRotatePitch)) {
				mc.player.setPitch(MathHelper.lerp(RenderUtils.getTickDelta(), pitch, lastRotatePitch));
			}
		}
	}

	@Override
	public void onRotationManagerPreRotate() {
		if (mc.player == null) {
			return;
		}

		yaw = mc.player.getYaw();
		pitch = mc.player.getPitch();
	}

	@Override
	public void onRotationManagerPreSendMovement() {
		if (mc.player == null) {
			return;
		}

		if (!Float.isNaN(rotateYaw)) {
			mc.player.setYaw(rotateYaw);
		}

		if (!Float.isNaN(rotatePitch)) {
			mc.player.setPitch(rotatePitch);
		}
	}

	@Override
	public void onRotationManagerPostSendMovement() {
		if (mc.player == null) {
			return;
		}

		mc.player.setYaw(yaw);
		mc.player.setPitch(pitch);

		lastRotateYaw = rotateYaw;
		lastRotatePitch = rotatePitch;

		rotateYaw = Float.NaN;
		rotatePitch = Float.NaN;
	}

	@Override
	public void onRotationManagerPrePacketSend(PacketEvent event) {
		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			onMovementPacketSend(event, packet);
		}
	}

	
	private void onMovementPacketSend(Cancellable event, PlayerMoveC2SPacket packet) {
		if (mc.player == null) {
			return;
		}

		if (sentYaw != mc.player.getYaw() || sentPitch != mc.player.getPitch()) {
			if (packet instanceof PlayerMoveC2SPacket.OnGroundOnly) {
				if (sentYaw != mc.player.getYaw() || sentPitch != mc.player.getPitch()) {
					event.cancel();

					PacketUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
							mc.player.getYaw(),
							mc.player.getPitch(),
							packet.isOnGround(),
							packet.horizontalCollision()
					));
				}
			}
		}
	}

	@Override
	public void onRotationManagerPreUpdateVelocity(UpdateVelocityEvent event) {
		if (mc.player == null) {
			return;
		}

		movementYaw = mc.player.getYaw();
	}

	@Override
	public void onRotationManagerPostUpdateVelocity() {
		if (mc.player == null) {
			return;
		}

		mc.player.setYaw(movementYaw);
	}

	@Override
	public void onPostPacketSend(PacketEvent event) {
		if (mc.player == null) {
			return;
		}

		if (event.getPacket() instanceof PlayerMoveC2SPacket packet) {
			float packetYaw = packet.getYaw(sentYaw);
			float packetPitch = packet.getPitch(sentPitch);

			sentYawDelta = packetYaw - sentYaw;
			sentPitchDelta = packetPitch - sentPitch;

			sentYaw = packetYaw;
			sentPitch = packetPitch;
		}
	}

	@Override
	public void onPreUpdateVelocity(UpdateVelocityEvent event) {
		if (mc.player == null) {
			return;
		}

		if (shouldMovementSync()) {
			event.cancel(false);

			mc.player.setVelocity(mc.player.getVelocity().add(MovementUtils.movementCorrection(
					rotateYaw,
					event.getMovementInput(),
					event.getSpeed()
			)));
		}
	}

	
	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.player == null) {
			return;
		}

		if (shouldMovementSync()) {
			float forward = mc.player.input.getMovementInput().y;
			float sideways = mc.player.input.getMovementInput().x;

			float delta = (mc.player.getYaw() - rotateYaw) * (float) (Math.PI / 180.0);
			float cos = MathHelper.cos(delta);
			float sin = MathHelper.sin(delta);

			float movementSideways = Math.round(sideways * cos - forward * sin);
			float movementForward = Math.round(forward * cos + sideways * sin);

			setMovementInput(event.forward, event.back, movementForward);
			setMovementInput(event.left, event.right, movementSideways);
		}
	}

	private void setMovementInput(MovementInputEvent.Input positive, MovementInputEvent.Input negative, float vector) {
		if (vector > 0.0f) {
			positive.set(true);
			negative.set(false);
		}
		else if (vector < 0.0f) {
			positive.set(false);
			negative.set(true);
		}
		else {
			positive.set(false);
			negative.set(false);
		}
	}

	@Override
	public void onPreJump(Cancellable event) {
		if (mc.player == null) {
			return;
		}

		if (shouldMovementSync()) {
			prevYaw = mc.player.getYaw();
			mc.player.setYaw(rotateYaw);
		}
	}

	@Override
	public void onPostJump() {
		if (mc.player == null) {
			return;
		}

		if (shouldMovementSync()) {
			mc.player.setYaw(prevYaw);
		}
	}

	public boolean isRotating() {
		return !Float.isNaN(rotateYaw) || !Float.isNaN(rotatePitch);
	}

	public void setRotate(Vec3d vec3d) {
		float rotateYaw = RotationUtils.getYaw(vec3d);
		float rotatePitch = RotationUtils.getPitch(vec3d);

		setRotate(rotateYaw, rotatePitch);
	}

	public void setRotate(Angle angle) {
		if (angle.isYawSet()) {
			this.rotateYaw = angle.getYaw();
		}
		if (angle.isPitchSet()) {
			this.rotatePitch = angle.getPitch();
		}
	}

	public void setRotate(float rotateYaw, float rotatePitch) {
		this.rotateYaw = rotateYaw;
		this.rotatePitch = rotatePitch;
	}

	public void packetRotate(Vec3d vec3d) {
		packetRotate(getYaw(vec3d), getPitch(vec3d));
	}

	public void packetRotate(Angle angle) {
		packetRotate(angle.getYaw(), angle.getPitch());
	}

	public void packetRotate(float yaw, float pitch) {
		if (mc.player == null) {
			return;
		}

		PacketUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
				yaw,
				pitch,
				mc.player.isOnGround(),
				mc.player.horizontalCollision
		));
	}

	private boolean shouldMovementSync() {
		return mc.player != null && !mc.player.isRiding() && clientConfig.getMovementSync() && !Float.isNaN(rotateYaw);
	}
}
