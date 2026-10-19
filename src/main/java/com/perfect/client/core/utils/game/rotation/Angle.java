package com.perfect.client.core.utils.game.rotation;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.Vec3d;

@Getter
@Setter
public class Angle {

	public static final Angle NONE = new Angle();

	private float yaw;
	private float pitch;

	public Angle() {
		this(Float.NaN, Float.NaN);
	}

	public Angle(Angle angle) {
		this(angle.getYaw(), angle.getPitch());
	}

	public Angle(Vec3d vec3d) {
		this(RotationUtils.getYaw(vec3d), RotationUtils.getPitch(vec3d));
	}

	public Angle(float yaw, float pitch) {
		set(yaw, pitch);
	}

	public void set(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public boolean isYawSet() {
		return !Float.isNaN(yaw);
	}

	public boolean isPitchSet() {
		return !Float.isNaN(pitch);
	}

	public boolean isSet() {
		return isYawSet() && isPitchSet();
	}

	public void reset() {
		yaw = Float.NaN;
		pitch = Float.NaN;
	}

	public Angle mul(float yaw, float pitch) {
		return new Angle(this.yaw * yaw, this.pitch * pitch);
	}

	public float[] toFloatArray() {
		return new float[]{yaw, pitch};
	}
}