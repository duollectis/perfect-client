package com.perfect.client.functional.function.combat.attackAura.helpers;

import com.perfect.client.core.functional.function.helper.FunctionHelper;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.Utils;
import com.perfect.client.core.utils.functions.RaycastUtils;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.core.utils.math.SecureRandomUtils;
import com.perfect.client.functional.function.combat.attackAura.AttackAura;
import com.perfect.client.functional.function.combat.attackAura.AttackAuraConfig;
import com.perfect.client.functional.function.combat.attackAura.data.AttackInfo;
import com.perfect.client.functional.function.combat.attackAura.data.AttackPoint;
import com.perfect.client.functional.function.combat.attackAura.data.AttackPointsFindResult;
import com.perfect.client.functional.function.combat.attackAura.data.PointCheckResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

//TODO: сделать чтобы легитный выбор позиции был плавным.


public class TargetHelper extends FunctionHelper<AttackAura, AttackAuraConfig> {

	private final static double LEGIT_BOX_EXPAND = 0.02;
	private final static double CLOSEST_BOX_EXPAND = 0.001;
	public final static double LEGIT_BOX_FIND_STEP = 0.1;

	public TargetHelper(AttackAura function, AttackAuraConfig config) {
		super(function, config);
	}

	
	public AttackInfo getAttackInfo() {
		if (mc.player == null || mc.world == null || mc.cameraEntity == null) {
			return null;
		}

		List<AttackInfo> attackInfos = new ArrayList<>();

		List<LivingEntity> targets = getTargets();

		for (LivingEntity target : targets) {
			AttackPoint point = getAttackPoint(target);

			if (point == null) {
				continue;
			}

			AttackInfo attackInfo = new AttackInfo(target, point);

			if (isTargeting(target)) {
				return attackInfo;
			}

			attackInfos.add(attackInfo);
		}

		if (attackInfos.isEmpty()) {
			return null;
		}

		attackInfos.sort(Comparator.comparing(info -> TargetUtils.getCompareValue(
				info.getEntity(),
				config.getPriority().get()
		)));

		return attackInfos.getFirst();
	}

	private boolean isTargeting(LivingEntity entity) {
		return false; //TODO: сделать таргетинг
	}

	private List<LivingEntity> getTargets() {
		return TargetUtils.getTargetsInRange(config.getTargetRange().get(), false, this::isGoodTarget);
	}

	private boolean isGoodTarget(LivingEntity entity) {
		if (entity.isDead() || !entity.isAlive()) {
			return false;
		}
		if (!config.getTargets().contains(entity.getType())) {
			return false;
		}
		if (config.getIgnoreInvisible().get() && EntityUtils.isInvisible(entity)) {
			return false;
		}

		if (entity instanceof PlayerEntity player) {
			if (config.getIgnoreNaked().get() && EntityUtils.isNaked(player)) {
				return false;
			}
		}
		else {
			if (config.getIgnoreNamed().get() && entity.hasCustomName()) {
				return false;
			}

			if (config.getIgnoreSitting().get()) {
				Entity vehicle = entity.getVehicle();

				if (vehicle instanceof BoatEntity || vehicle instanceof MinecartEntity) {
					return false;
				}
			}
		}

		if (config.getIgnorePassive().get()) {
			if (entity instanceof EndermanEntity enderman && !enderman.isAngry()) {
				return false;
			}
			if (entity instanceof ZombifiedPiglinEntity piglin && !piglin.isAttacking()) {
				return false;
			}
			if (entity instanceof WolfEntity wolf && !wolf.isAttacking()) {
				return false;
			}
		}

		return RotationUtils.getYawRange(entity.getPos()) <= config.getFieldOfView().get();
	}

	private AttackPoint getAttackPoint(LivingEntity entity) {
		if (config.getLegitRotate().get()) {
			return getLegitAttackPoint(entity);
		}
		else {
			return getClosestAttackPoint(entity);
		}
	}

	private AttackPoint getClosestAttackPoint(LivingEntity entity) {
		if (mc.player == null) {
			return null;
		}

		Box box = entity.getBoundingBox().expand(-CLOSEST_BOX_EXPAND);

		Vec3d closestPoint = PositionMath.closestVec3d(mc.player.getEyePos(), box);

		if (PositionMath.isOutOfRange(closestPoint, config.getTargetRange().get())) {
			return null;
		}

		if (isGoodAttackPoint(entity, closestPoint).isGood()) {
			return AttackPoint.fromWorld(closestPoint, box);
		}
		else {
			AttackPointsFindResult attackPointsFindResult = getAttackPoints(entity, box);

			List<AttackPoint> points = attackPointsFindResult.getPoints();

			if (points.isEmpty()) {
				return null;
			}

			sortByAttackRange(points, entity.getBoundingBox());

			points.sort(Comparator.comparing(point -> PositionMath.squareDistance(point.toWorldVec3d(entity.getPos()))));

			return points.getFirst();
		}
	}

	private void sortByAttackRange(List<AttackPoint> points, Box box) {
		Utils.keepHigherValues(points, point -> isInAttackRange(point.toWorldVec3d(box)) ? 1.0 : 0.0);
	}

	private boolean isInAttackRange(Vec3d pos) {
		return PositionMath.isOutOfRange(pos, config.getAttackRange().get());
	}

	private AttackPoint getLegitAttackPoint(LivingEntity entity) {
		Box box = entity.getBoundingBox().expand(-LEGIT_BOX_EXPAND);
		AttackPointsFindResult attackPointsFindResult = getAttackPoints(entity, box);
		List<AttackPoint> points = attackPointsFindResult.getPoints();

		if (points.isEmpty()) {
			return null;
		}

		sortByAttackRange(points, entity.getBoundingBox());

		int randomPointIndex = SecureRandomUtils.random(0, points.size() - 1);

		return points.get(randomPointIndex);
	}

	public AttackPointsFindResult getAttackPoints(LivingEntity entity, Box box) {
		AttackPointsFindResult attackPointsFindResult = new AttackPointsFindResult();

		iterateVisibleHitbox(box, point -> {
			attackPointsFindResult.increaseTotal();

			PointCheckResult pointCheckResult = isGoodAttackPoint(entity, point);

			if (pointCheckResult.isInvisible()) {
				attackPointsFindResult.increaseInvisible();
			}
			else if (pointCheckResult.isGood()) {
				attackPointsFindResult.add(point);
			}
		});

		attackPointsFindResult.end();
		return attackPointsFindResult;
	}

	
	private void iterateVisibleHitbox(Box box, Consumer<AttackPoint> attackPointConsumer) {
		if (mc.player == null) {
			return;
		}

		Vec3d eye = mc.player.getEyePos();

		boolean isIn = box.contains(eye);

		if (eye.x > box.maxX || isIn) {
			iterateHitboxXPlane(1.0, box, attackPointConsumer);
		}
		if (eye.x < box.minX || isIn) {
			iterateHitboxXPlane(0.0, box, attackPointConsumer);
		}

		if (eye.y > box.maxY || isIn) {
			iterateHitboxYPlane(1.0, box, attackPointConsumer);
		}
		if (eye.y < box.minY || isIn) {
			iterateHitboxYPlane(0.0, box, attackPointConsumer);
		}

		if (eye.z > box.maxZ || isIn) {
			iterateHitboxZPlane(1.0, box, attackPointConsumer);
		}
		if (eye.z < box.minZ || isIn) {
			iterateHitboxZPlane(0.0, box, attackPointConsumer);
		}
	}

	private void iterateHitboxXPlane(double x, Box box, Consumer<AttackPoint> attackPointConsumer) {
		for (double y = 0.0; y <= 1.0; y += LEGIT_BOX_FIND_STEP) {
			for (double z = 0.0; z <= 1.0; z += LEGIT_BOX_FIND_STEP) {
				attackPointConsumer.accept(new AttackPoint(box, x, randAxis(y), randAxis(z)));
			}
		}
	}

	private void iterateHitboxYPlane(double y, Box box, Consumer<AttackPoint> attackPointConsumer) {
		for (double x = 0.0; x <= 1.0; x += LEGIT_BOX_FIND_STEP) {
			for (double z = 0.0; z <= 1.0; z += LEGIT_BOX_FIND_STEP) {
				attackPointConsumer.accept(new AttackPoint(box, randAxis(x), y, randAxis(z)));
			}
		}
	}

	private void iterateHitboxZPlane(double z, Box box, Consumer<AttackPoint> attackPointConsumer) {
		for (double x = 0.0; x <= 1.0; x += LEGIT_BOX_FIND_STEP) {
			for (double y = 0.0; y <= 1.0; y += LEGIT_BOX_FIND_STEP) {
				attackPointConsumer.accept(new AttackPoint(box, randAxis(x), randAxis(y), z));
			}
		}
	}

	private double randAxis(double axis) {
		if (!config.getLegitRotate().get()) {
			return axis;
		}

		double offset = LEGIT_BOX_FIND_STEP / 2.0;
		return SecureRandomUtils.random(Math.max(axis - offset, 0.0), Math.min(axis - offset, 1.0));
	}

	public PointCheckResult isGoodAttackPoint(LivingEntity entity, AttackPoint point) {
		Vec3d vec3d = point.toWorldVec3d(entity.getPos());
		return isGoodAttackPoint(entity, vec3d);
	}

	private PointCheckResult isGoodAttackPoint(LivingEntity entity, Vec3d vec3d) {
		assert mc.player != null;

		if (PositionMath.isOutOfRange(vec3d, config.getTargetRange().get())) {
			return new PointCheckResult(false, false);
		}

		Vec3d start = mc.player.getCameraPosVec(RenderUtils.getTickDelta());

		if (config.getRaytraceBlocks().get() && raytraceBlock(start, vec3d)) {
			return new PointCheckResult(false, true);
		}
		if (config.getRaytraceEntities().get() && raytraceEntity(entity, start, vec3d)) {
			return new PointCheckResult(false, true);
		}
		return new PointCheckResult(true, false);
	}

	private boolean raytraceEntity(LivingEntity entity, Vec3d start, Vec3d end) {
		EntityHitResult entityHit = raytraceEntityResult(start, end);

		if (entityHit == null) {
			return false;
		}

		if (entityHit.getEntity().equals(entity)) {
			return false;
		}

		return entityHit.getType() != HitResult.Type.MISS;
	}

	public EntityHitResult raytraceEntityResult(Vec3d start, Vec3d end) {
		if (mc.cameraEntity == null) {
			return null;
		}

		Box box = RaycastUtils.getBox(start, end);

		return ProjectileUtil.raycast(
				mc.cameraEntity,
				start,
				end,
				box,
				EntityPredicates.CAN_HIT,
				start.squaredDistanceTo(end)
		);
	}

	public boolean raytraceBlock(Vec3d start, Vec3d end) {
		if (mc.player == null || mc.world == null) {
			return false;
		}

		BlockHitResult blockHit = mc.world.raycast(new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.OUTLINE,
				RaycastContext.FluidHandling.NONE,
				mc.player
		));

		return blockHit.getType() != HitResult.Type.MISS;
	}
}
