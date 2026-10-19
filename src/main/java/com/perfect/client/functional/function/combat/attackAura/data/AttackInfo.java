package com.perfect.client.functional.function.combat.attackAura.data;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Getter
public class AttackInfo {

	private final LivingEntity entity;
	private final AttackPoint attackPoint;

	public AttackInfo(LivingEntity entity, AttackPoint attackPoint) {
		this.entity = entity;
		this.attackPoint = attackPoint;
	}

	public Vec3d getAttackVec3d() {
		return getAttackVec3d(entity.getPos());
	}

	public Vec3d getAttackVec3d(Vec3d pos) {
		return attackPoint.toWorldVec3d(pos);
	}
}
