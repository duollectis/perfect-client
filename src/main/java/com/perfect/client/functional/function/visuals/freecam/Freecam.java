package com.perfect.client.functional.function.visuals.freecam;

import com.perfect.client.core.event.events.input.keyboard.MovementInputEvent;
import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.RaycastUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.inject.mixinInterface.IVec3d;
import lombok.Getter;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import static com.perfect.client.manager.system.FunctionManager.freecam;

public class Freecam extends Function {

	private final FreecamConfig config = new FreecamConfig(this);

	public Freecam() {
		super("freecam", "Freecam", Category.VISUALS, "Free Camera");
	}

	public void changeControl() {
		playerControl = !playerControl;
	}

	@Getter
	private boolean playerControl;

	private double fSpeed, bSpeed, rSpeed, lSpeed, uSpeed, dSpeed;

	public final Vector3d pos = new Vector3d();
	public final Vector3d lastPos = new Vector3d();

	public float yaw, pitch;
	public float lastYaw, lastPitch;

	private boolean updatingCrosshairTarget;

	@Override
	public void onEnable() {
		if (mc.player == null) {
			disable();
			return;
		}

		playerControl = false;

		yaw = mc.player.getYaw();
		pitch = mc.player.getPitch();
		lastYaw = yaw;
		lastPitch = pitch;

		Utils.set(pos, mc.gameRenderer.getCamera().getPos());
		Utils.set(lastPos, mc.gameRenderer.getCamera().getPos());

		fSpeed = 0;
		bSpeed = 0;
		rSpeed = 0;
		lSpeed = 0;
		uSpeed = 0;
		dSpeed = 0;
	}

	@Override
	public void onUpdateCrosshairTarget(Cancellable event) {
		if (!config.getInteractions().get()) {
			event.cancel();
			mc.crosshairTarget = RaycastUtils.createMissed();
			return;
		}

		if (updatingCrosshairTarget) {
			return;
		}

		event.cancel();

		Entity camera = mc.getCameraEntity();

		if (camera == null) {
			return;
		}

		double x = camera.getX();
		double y = camera.getY();
		double z = camera.getZ();
		double lastX = camera.lastX;
		double lastY = camera.lastY;
		double lastZ = camera.lastZ;
		float yaw = camera.getYaw();
		float pitch = camera.getPitch();
		float lastYaw = camera.lastYaw;
		float lastPitch = camera.lastPitch;

		((IVec3d) camera.getPos()).perfect$set(
				freecam.pos.x,
				freecam.pos.y - camera.getEyeHeight(camera.getPose()),
				freecam.pos.z
		);

		camera.lastX = freecam.lastPos.x;
		camera.lastY = freecam.lastPos.y - camera.getEyeHeight(camera.getPose());
		camera.lastZ = freecam.lastPos.z;
		camera.setYaw(freecam.yaw);
		camera.setPitch(freecam.pitch);
		camera.lastYaw = freecam.lastYaw;
		camera.lastPitch = freecam.lastPitch;

		updatingCrosshairTarget = true;

		if (!freecam.isPlayerControl()) {
			mc.gameRenderer.updateCrosshairTarget(RenderUtils.getTickDelta());
		}

		updatingCrosshairTarget = false;

		((IVec3d) camera.getPos()).perfect$set(x, y, z);
		camera.lastX = lastX;
		camera.lastY = lastY;
		camera.lastZ = lastZ;
		camera.setYaw(yaw);
		camera.setPitch(pitch);
		camera.lastYaw = lastYaw;
		camera.lastPitch = lastPitch;
	}

	@Override
	public void onRotate() {
		if (!config.getInteractions().get()
				|| !config.getRotate().get()
				|| mc.player == null
				|| mc.world == null
				|| mc.crosshairTarget == null
				|| playerControl) {
			return;
		}

		BlockPos crossHairPos;
		Vec3d crossHairPosition;

		if (mc.crosshairTarget instanceof EntityHitResult result) {
			Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(result.getPos()));
			Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(result.getPos()));
		}
		else {
			crossHairPosition = mc.crosshairTarget.getPos();
			crossHairPos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();

			if (BlockUtils.isAir(crossHairPos)) {
				return;
			}

			Managers.ROTATION.setRotateYaw(RotationUtils.getYaw(crossHairPosition));
			Managers.ROTATION.setRotatePitch(RotationUtils.getPitch(crossHairPosition));
		}
	}

	@Override
	public void onPostTick() {
		if (mc.world == null) {
			return;
		}

		lastPos.set(pos);

		if (playerControl) {
			return;
		}

		boolean forward = mc.options.forwardKey.isPressed();
		boolean backward = mc.options.backKey.isPressed();
		boolean left = mc.options.leftKey.isPressed();
		boolean right = mc.options.rightKey.isPressed();
		boolean up = mc.options.jumpKey.isPressed();
		boolean down = mc.options.sneakKey.isPressed();

		Vec3d forwardSpeed = Vec3d.fromPolar(0, yaw);
		Vec3d sideSpeed = Vec3d.fromPolar(0, yaw + 90);
		double velX = 0;
		double velY = 0;
		double velZ = 0;

		boolean movedForward = false;
		boolean movedSide = false;

		if (forward && !backward) {
			float smoothness = Math.min((float) config.getSmoothness().fromPercent(), 0.98f);
			fSpeed = MathUtils.interpolate(fSpeed, 1, 1 - smoothness);
			velX += forwardSpeed.x * config.getSpeed().fromBPS() * fSpeed;
			velZ += forwardSpeed.z * config.getSpeed().fromBPS() * fSpeed;
			movedForward = true;
		}
		else {
			fSpeed = 0;
		}

		if (backward && !forward) {
			bSpeed = MathUtils.interpolate(bSpeed, 1, 1 - config.getSpeed().fromPercent());
			velX -= forwardSpeed.x * config.getSpeed().fromBPS() * bSpeed;
			velZ -= forwardSpeed.z * config.getSpeed().fromBPS() * bSpeed;
			movedForward = true;
		}
		else {
			bSpeed = 0;
		}

		if (right && !left) {
			rSpeed = MathUtils.interpolate(rSpeed, 1, 1 - config.getSpeed().fromPercent());
			velX += sideSpeed.x * config.getSpeed().fromBPS() * rSpeed;
			velZ += sideSpeed.z * config.getSpeed().fromBPS() * rSpeed;
			movedSide = true;
		}
		else {
			rSpeed = 0;
		}

		if (left && !right) {
			lSpeed = MathUtils.interpolate(lSpeed, 1, 1 - config.getSpeed().fromPercent());
			velX -= sideSpeed.x * config.getSpeed().fromBPS() * lSpeed;
			velZ -= sideSpeed.z * config.getSpeed().fromBPS() * lSpeed;
			movedSide = true;
		}
		else {
			lSpeed = 0;
		}

		if (movedForward && movedSide) {
			double diagonal = 1 / Math.sqrt(2);
			velX *= diagonal;
			velZ *= diagonal;
		}

		if (up && !down) {
			uSpeed = MathUtils.interpolate(uSpeed, 1, 1 - config.getSpeed().fromPercent());
			velY += config.getSpeed().fromBPS() * uSpeed;
		}
		else {
			uSpeed = 0;
		}

		if (down && !up) {
			dSpeed = MathUtils.interpolate(dSpeed, 1, 1 - config.getSpeed().fromPercent());
			velY -= config.getSpeed().fromBPS() * dSpeed;
		}
		else {
			dSpeed = 0;
		}

		pos.set(pos.x + velX, pos.y + velY, pos.z + velZ);
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		if (mc.world == null) {
			return;
		}
		if (event.getPacket() instanceof DeathMessageS2CPacket packet) {
			Entity entity = mc.world.getEntityById(packet.playerId());
			if (entity == mc.player) {
				disable();
			}
		}
	}

	@Override
	public void onGameLeft() {
		disable();
	}

	@Override
	public void onChunkOcclusion(Cancellable event) {
		event.cancel();
	}

	@Override
	public void onMovementInput(MovementInputEvent event) {
		if (mc.world == null || mc.currentScreen != null) {
			return;
		}

		if (playerControl) {
			return;
		}

		event.setAllInput(false);
	}

	public boolean noHands() {
		return isEnabled() && config.getNoHandRender().get();
	}

	public void changeLookDirection(double deltaX, double deltaY) {
		yaw += (float) deltaX;
		pitch += (float) deltaY;
		pitch = MathHelper.clamp(pitch, -90, 90);
	}

	public double getX() {
		return MathHelper.lerp(RenderUtils.getTickDelta(), lastPos.x, pos.x);
	}

	public double getY() {
		return MathHelper.lerp(RenderUtils.getTickDelta(), lastPos.y, pos.y);
	}

	public double getZ() {
		return MathHelper.lerp(RenderUtils.getTickDelta(), lastPos.z, pos.z);
	}

	public float getYaw() {
		float value = MathHelper.lerp(RenderUtils.getTickDelta(), lastYaw, yaw);
		lastYaw = yaw;
		return value;
	}

	public float getPitch() {
		float value = MathHelper.lerp(RenderUtils.getTickDelta(), lastPitch, pitch);
		lastPitch = pitch;
		return value;
	}
}
