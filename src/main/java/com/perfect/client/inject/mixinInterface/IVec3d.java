package com.perfect.client.inject.mixinInterface;

import net.minecraft.util.math.Vec3d;

public interface IVec3d {

	void perfect$set(double x, double y, double z);

	void perfect$set(Vec3d vec3d);

	void perfect$setY(double y);

	void perfect$setX(double var1);

	void perfect$setZ(double var1);
}
