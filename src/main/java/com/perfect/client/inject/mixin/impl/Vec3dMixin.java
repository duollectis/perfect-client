package com.perfect.client.inject.mixin.impl;

import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({Vec3d.class})
public class Vec3dMixin implements IVec3d {

	@Shadow
	@Final
	@Mutable
	public double x;

	@Shadow
	@Final
	@Mutable
	public double y;

	@Shadow
	@Final
	@Mutable
	public double z;

	@Override
	public void perfect$setX(double x) {
		this.x = x;
	}

	@Override
	public void perfect$setY(double y) {
		this.y = y;
	}

	@Override
	public void perfect$setZ(double z) {
		this.z = z;
	}

	@Override
	public void perfect$set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void perfect$set(Vec3d vec3d) {
		this.x = vec3d.x;
		this.y = vec3d.y;
		this.z = vec3d.z;
	}
}
