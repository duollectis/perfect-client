package com.perfect.client.functional.function.combat.attackAura.data;

import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class AttackPoint {

	private final Vec3d vec3d;
	private final Box box;

	public AttackPoint(Box box, double x, double y, double z) {
		this.vec3d = new Vec3d(x, y, z);
		this.box = box;
	}

	public Vec3d toWorldVec3d(Vec3d boxPos) {
		Box box = PositionMath.getBoxAt(this.box, boxPos);
		return toWorldVec3d(box);
	}

	public Vec3d toWorldVec3d(Box box) {
		return new Vec3d(
				box.minX + box.getLengthX() * vec3d.x,
				box.minY + box.getLengthY() * vec3d.y,
				box.minZ + box.getLengthZ() * vec3d.z
		);
	}

	public static AttackPoint fromWorld(Vec3d vec3d, Box box) {
		return new AttackPoint(
				box,
				(vec3d.getX() - box.minX) / box.getLengthX(),
				(vec3d.getY() - box.minY) / box.getLengthY(),
				(vec3d.getZ() - box.minZ) / box.getLengthZ()
		);
	}
}
