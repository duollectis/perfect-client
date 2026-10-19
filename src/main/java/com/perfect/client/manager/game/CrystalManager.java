package com.perfect.client.manager.game;

import com.perfect.client.core.event.events.entity.EntityEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.collection.UpdatingCollection;
import com.perfect.client.core.utils.game.world.WorldUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.List;

public class CrystalManager extends Manager {

	private final HashMap<EndCrystalEntity, Long> map = new HashMap<>();
	private final UpdatingCollection<Entity> killedEntities = new UpdatingCollection<>();

	@Override
	public void onGameLeft() {
		map.clear();
		killedEntities.clear();
	}

	@Override
	public void onCrystalManagerEntitySpawn(EntityEvent event) {
		if (event.getEntity() instanceof EndCrystalEntity crystal) {
			map.put(crystal, System.currentTimeMillis());
		}
	}

	@Override
	public void onEntityRemove(EntityEvent event) {
		if (event.getEntity() instanceof EndCrystalEntity crystal) {
			map.remove(crystal);
		}
	}

	@Override
	public void onPostTick() {
		killedEntities.update(1.5d);
	}

	public void onAttackCrystal(EndCrystalEntity crystal) {
		for (Entity entity : getNearbyEntities(crystal)) {
			if (entity == null) {
				continue;
			}
			if (isKilledByCrystal(crystal, entity)) {
				killedEntities.put(entity);
			}
		}
	}

	private boolean isKilledByCrystal(EndCrystalEntity crystal, Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return true;
		}
		if (entity instanceof ItemEntity) {
			return PositionMath.simpleDistance(entity.getBlockPos(), crystal.getBlockPos()) <= 1;
		}
		return false;
	}

	private List<Entity> getNearbyEntities(EndCrystalEntity crystal) {
		Box box = PositionMath.boxFromCenter(5, crystal.getBlockPos().toCenterPos());
		return WorldUtils.getEntities(box);
	}

	public boolean isBlownUp(Entity entity) {
		return killedEntities.containsKey(entity);
	}

	public long getAge(EndCrystalEntity crystal) {
		return System.currentTimeMillis() - map.getOrDefault(crystal, 0L);
	}
}
