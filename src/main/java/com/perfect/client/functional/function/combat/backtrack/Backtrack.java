package com.perfect.client.functional.function.combat.backtrack;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.collection.ListRemoveHashMap;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.backtrack.data.BacktrackInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;


public class Backtrack extends Function {

	private final BacktrackConfig config = new BacktrackConfig(this);

	private final ListRemoveHashMap<Integer, List<BacktrackInfo>> backtrackMap = new ListRemoveHashMap<>();

	public Backtrack() {
		super("backtrack", "Backtrack", Category.COMBAT);
	}

	@Override
	public void onDisable() {
		reset();
	}

	private void reset() {
		backtrackMap.clear();
	}

	@Override
	public void onTickEntities() {
		if (mc.world == null) {
			reset();
			return;
		}

		List<LivingEntity> targets = getTargetsInRange();

		tickBacktracks(targets);
		addBacktracks(targets);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		for (Map.Entry<Integer, List<BacktrackInfo>> entry : backtrackMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			for (BacktrackInfo backtrackInfo : entry.getValue()) {
				BoxRenderer.drawBox(
						event.getMatrices(),
						backtrackInfo.getHitbox(),
						config.getLineColor().get(),
						config.getSideColor().get(),
						config.getCosmetics().get().bundle()
				);
			}
		}
	}

	
	private void tickBacktracks(List<LivingEntity> targets) {
		assert mc.world != null;

		List<Integer> entitiesToRemove = new ArrayList<>();

		for (Map.Entry<Integer, List<BacktrackInfo>> entry : backtrackMap.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			int entityId = entry.getKey();
			List<BacktrackInfo> backtracks = entry.getValue();

			Entity entity = mc.world.getEntityById(entityId);

			if (!(entity instanceof LivingEntity) || !targets.contains(entity)) {
				entitiesToRemove.add(entityId);
				continue;
			}

			List<BacktrackInfo> backtracksToRemove = new ArrayList<>();

			for (BacktrackInfo backtrackInfo : backtracks) {
				if (backtrackInfo.getTicks() >= config.getBacktrackTicks().get()) {
					backtracksToRemove.add(backtrackInfo);
					continue;
				}

				backtrackInfo.tick();
			}

			backtracks.removeAll(backtracksToRemove);
		}

		backtrackMap.removeAll(entitiesToRemove);
	}

	
	private void addBacktracks(List<LivingEntity> targets) {
		for (LivingEntity entity : targets) {
			if (!EntityUtils.isMoving(entity)) {
				continue;
			}

			int entityId = entity.getId();

			if (!backtrackMap.containsKey(entityId)) {
				Box prevHitbox = EntityUtils.getBoxAt(entity, EntityUtils.getPrevPos(entity));
				addBacktrack(entityId, prevHitbox);
			}

			Box hitbox = entity.getBoundingBox();

			addBacktrack(entityId, hitbox);
		}
	}

	private void addBacktrack(int entityId, Box hitbox) {
		if (!isOutOfRange(hitbox)) {
			BacktrackInfo backtrackInfo = new BacktrackInfo(hitbox);

			List<BacktrackInfo> backtrackInfoList = backtrackMap.get(entityId);

			if (backtrackInfoList == null) {
				backtrackInfoList = new ArrayList<>();
				backtrackInfoList.add(backtrackInfo);

				backtrackMap.put(entityId, backtrackInfoList);
			}
			else {
				backtrackInfoList.add(backtrackInfo);
			}
		}
	}

	private List<LivingEntity> getTargetsInRange() {
		return TargetUtils.getTargetsInRange(config.getRange().get() * 2, false, this::isGoodTarget);
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (config.getOnlyPlayers().get()) {
			return entity instanceof PlayerEntity;
		}
		return true;
	}

	private boolean isOutOfRange(Box box) {
		return PositionMath.isOutOfRange(getClosestPos(box), config.getRange().get());
	}

	private Vec3d getClosestPos(Box box) {
		if (mc.player == null) {
			return Vec3d.ZERO;
		}
		return PositionMath.closestVec3d(mc.player.getEyePos(), box);
	}

	private List<Box> getHitboxes(Entity entity, Box defaultHitbox) {
		List<Box> hitboxList = new ArrayList<>();
		hitboxList.add(defaultHitbox);

		List<BacktrackInfo> backtracks = backtrackMap.get(entity.getId());

		if (backtracks != null) {
			for (BacktrackInfo backtrackInfo : backtracks) {
				hitboxList.add(backtrackInfo.getHitbox());
			}
		}

		return hitboxList;
	}

	public EntityHitResult raycast(
			Entity fromEntity,
			Vec3d min,
			Vec3d max,
			Box box,
			Predicate<Entity> predicate,
			double maxDistance
	) {
		if (mc.world == null) {
			return null;
		}

		double d = maxDistance;
		Entity finalEntity = null;
		Vec3d finalVec3d = null;

		for (Entity checkEntity : mc.world.getOtherEntities(fromEntity, box, predicate)) {
			List<Box> hitboxes = getHitboxes(
					checkEntity,
					checkEntity.getBoundingBox().expand(checkEntity.getTargetingMargin())
			);

			for (Box hitbox : hitboxes) {
				Optional<Vec3d> raycastPos = hitbox.raycast(min, max);
				if (hitbox.contains(min)) {
					if (d >= 0.0) {
						finalEntity = checkEntity;
						finalVec3d = raycastPos.orElse(min);
						d = 0.0;
					}
				}
				else if (raycastPos.isPresent()) {
					Vec3d vec3d2 = raycastPos.get();
					double e = min.squaredDistanceTo(vec3d2);
					if (e < d || d == 0.0) {
						if (checkEntity.getRootVehicle() == fromEntity.getRootVehicle()) {
							if (d == 0.0) {
								finalEntity = checkEntity;
								finalVec3d = vec3d2;
							}
						}
						else {
							finalEntity = checkEntity;
							finalVec3d = vec3d2;
							d = e;
						}
					}
				}
			}
		}

		if (finalEntity == null) {
			return null;
		}

		return new EntityHitResult(finalEntity, finalVec3d);
	}
}
