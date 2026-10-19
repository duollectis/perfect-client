package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileManager extends Manager {

	private final Map<Integer, Integer> owners = new ConcurrentHashMap<>();

	@Override
	public void onPreTick() {
		owners.forEach((projectileId, ownerId) -> {
			if (!EntityUtils.isExist(projectileId) || !EntityUtils.isExist(ownerId)) {
				owners.remove(projectileId);
			}
		});
	}

	@Override
	public void onProjectileManagerEntitySpawn(EntityEvent event) {
		if (event.getEntity() instanceof ProjectileEntity entity) {
			Entity nearest = getNearestEntity(entity.getPos());

			if (nearest != null) {
				owners.put(entity.getId(), nearest.getId());
			}
		}
	}

	public Entity getOwner(Entity projectile) {
		if (mc.world == null) {
			return null;
		}

		Integer id = owners.get(projectile.getId());

		if (id == null) {
			return null;
		}

		return mc.world.getEntityById(id);
	}

	public int getOwnerId(int projectileId) {
		return owners.get(projectileId);
	}

	private Entity getNearestEntity(Vec3d pos) {
		Entity nearest = null;
		double minDistance = 0.0;

		for (Entity entity : BlockUtils.getEntities(PositionMath.boxFromCenter(5.0, pos))) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}

			double distance = PositionMath.squareDistance(entity.getPos(), pos);

			if (nearest == null || distance < minDistance) {
				nearest = entity;
				minDistance = distance;
			}
		}

		return nearest;
	}
}
