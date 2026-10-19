package com.perfect.client.manager.game.predict;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.core.utils.functions.CollisionUtils;
import com.perfect.client.core.utils.functions.hole.HoleUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.perfect.client.manager.system.FunctionManager.fakePlayer;


public class PredictManager extends Manager {

	public static final double GRAVITY = 0.8;

	private final Map<Entity, MovementInfo> movementInfos = new ConcurrentHashMap<>();
	private final Map<Entity, PredictState> predictCache = new ConcurrentHashMap<>();

	@Override
	public void onGameLeft() {
		movementInfos.clear();
		predictCache.clear();
	}

	@Override
	public void onPostSendMovement() {
		onEntityUpdate(mc.player);

		if (fakePlayer.isEnabled()) {
			onEntityUpdate(fakePlayer.getFakePlayer());
		}
	}

	@Override
	public void onPreTick() {
		if (mc.world == null) {
			return;
		}

		List<Entity> toRemoveList = new ArrayList<>();

		predictCache.keySet().forEach(entity -> {
			if (!EntityUtils.isExist(entity)) {
				toRemoveList.add(entity);
			}
		});

		movementInfos.keySet().forEach(entity -> {
			if (!EntityUtils.isExist(entity)) {
				toRemoveList.add(entity);
			}
		});

		for (Entity toRemove : toRemoveList) {
			predictCache.remove(toRemove);
			movementInfos.remove(toRemove);
		}
	}

	@Override
	public void onPostPacketReceive(PacketEvent event) {
		if (mc.world == null) {
			return;
		}

		if (event.getPacket() instanceof EntityPositionSyncS2CPacket packet) {
			onEntityUpdate(packet.id());
		}
		else if (event.getPacket() instanceof EntityPositionS2CPacket packet) {
			onEntityUpdate(packet.entityId());
		}
		else if (event.getPacket() instanceof EntityS2CPacket packet) {
			onEntityUpdate(packet.getEntity(mc.world));
		}
	}

	public ExtrapolationResult extrapolate(Entity entity, int ticks) {
		if (ticks <= 0) {
			return new ExtrapolationResult(entity.getPos(), entity.getBoundingBox());
		}

		PredictState cachedPredictState = predictCache.get(entity);

		int predicted = cachedPredictState != null ? cachedPredictState.getList().size() - 1 : 0;

		if (predicted >= ticks) {
			return cachedPredictState.getList().get(ticks);
		}

		if (entity instanceof PlayerEntity player && isWalking(player)) {
			MovementInfo movementInfo = movementInfos.get(player);

			if (movementInfo != null) {
				if (predicted > 0) {
					return addCacheAndReturnBox(
							entity,
							extrapolateWalking(cachedPredictState, player, movementInfo, ticks - predicted, false)
					);
				}
				else {
					return addCacheAndReturnBox(entity, extrapolateWalking(player, movementInfo, ticks));
				}
			}
		}

		if (predicted > 0) {
			return addCacheAndReturnBox(
					entity,
					extrapolateLinear(entity, cachedPredictState, ticks - predicted, false)
			);
		}
		else {
			return addCacheAndReturnBox(entity, extrapolateLinear(entity, ticks));
		}
	}

	private ExtrapolationResult addCacheAndReturnBox(Entity entity, PredictState predictState) {
		if (!predictCache.containsKey(entity)) {
			predictCache.put(entity, predictState);
		}
		return predictState.getList().getLast();
	}

	private PredictState extrapolateWalking(PlayerEntity player, MovementInfo movementInfo, int ticks) {
		Vec3d startVelocity = EntityUtils.getVelocity(player);

		boolean onGround = player.isOnGround();

		if (onGround) {
			startVelocity = new Vec3d(startVelocity.x, 0, startVelocity.z);
		}

		PredictState predictState = new PredictState(player.getPos(), startVelocity, startVelocity, onGround, onGround);

		return extrapolateWalking(predictState, player, movementInfo, ticks, true);
	}

	private PredictState extrapolateWalking(
			PredictState predictState,
			PlayerEntity player,
			MovementInfo movementInfo,
			int ticks,
			boolean start
	) {
		if (mc.player == null) {
			return predictState;
		}

		for (int tick = 0; tick < ticks; tick++) {
			// Игрок, который бежит в холку, в большинстве случаев в ней и остановится.

			Vec3d pos = predictState.getPos();

			Box box = EntityUtils.getBoxAt(player, pos);

			if (start || tick != 0) {
				predictState.add(new ExtrapolationResult(pos, box));
			}

			if (HoleUtils.isInHole(BlockPos.ofFloored(pos))) {
				break;
			}

			double d = predictState.getVelocity().getY() - GRAVITY;

			predictState.setVelocity(new Vec3d(
					predictState.getVelocity().x * 0.91,
					d * 0.98,
					predictState.getVelocity().z * 0.91
			));

			if (predictState.isOnGround()) {
				predictState.setVelocity(new Vec3d(
						predictState.getStartVelocity().x,
						predictState.getVelocity().y,
						predictState.getStartVelocity().z
				));
			}

			Vec3d movement = predictState.getVelocity();

			if (predictState.isLastOnGround()
					&& predictState.getVelocity().y <= 0.0
					&& isFastFall(player, box, movementInfo.getFastFallHeight())
			) {
				// Мы не будем рассчитывать скорость FastFall.
				// Хватит взять то, что он моментальный.

				movement = new Vec3d(movement.x, -100.0f, movement.z);
			}

			movement = CollisionUtils.adjustMovementForCollisions(
					player,
					box,
					movement,
					movementInfo.getStepHeight(),
					predictState.isOnGround()
			);

			predictState.setLastOnGround(predictState.isOnGround());
			predictState.setOnGround(movement.y > predictState.getVelocity().y);

			predictState.move(movement);
		}

		predictState.add(player, predictState.getPos());

		return predictState;
	}

	private PredictState extrapolateLinear(Entity entity, int ticks) {
		Vec3d velocity = EntityUtils.getVelocity(entity);
		boolean onGround = entity.isOnGround();

		PredictState predictState = new PredictState(
				entity.getPos(),
				velocity,
				velocity,
				onGround,
				onGround
		);

		return extrapolateLinear(entity, predictState, ticks, true);
	}

	private PredictState extrapolateLinear(Entity entity, PredictState predictState, int ticks, boolean start) {
		for (int tick = 0; tick < ticks; tick++) {
			Box box = EntityUtils.getBoxAt(entity, predictState.getPos());

			if (start || tick != 0) {
				predictState.add(new ExtrapolationResult(predictState.getPos(), box));
			}

			Vec3d movement = predictState.getVelocity();

			movement = CollisionUtils.adjustMovementForCollisions(
					entity,
					box,
					movement,
					0.6f,
					predictState.isOnGround()
			);

			predictState.move(movement);
		}

		return predictState;
	}

	private boolean isFastFall(Entity entity, Box box, float fastFallHeight) {
		if (entity == null || mc.world == null) {
			return false;
		}

		if (BlockUtils.isInWeb(box)) {
			return false;
		}

		if (!mc.world.isSpaceEmpty(entity, box.stretch(0, -0.01, 0))) {
			return false;
		}

		for (double i = 0.02; i < fastFallHeight + 0.03; i += 0.01) {
			if (!mc.world.isSpaceEmpty(entity, box.offset(0.0, -i, 0.0))) {
				return true;
			}
		}

		return false;
	}

	private void onEntityUpdate(int entityId) {
		if (mc.world == null) {
			return;
		}

		onEntityUpdate(mc.world.getEntityById(entityId));
	}

	private void onEntityUpdate(Entity entity) {
		if (entity == null) {
			return;
		}

		predictCache.remove(entity);

		if (entity instanceof PlayerEntity player && isWalking(player)) {
			if (!movementInfos.containsKey(entity)) {
				movementInfos.put(entity, new MovementInfo());
			}

			MovementInfo movementInfo = movementInfos.get(entity);
			movementInfo.update(player);
		}
	}

	private boolean isWalking(PlayerEntity player) {
		return !player.isTouchingWater()
				&& !player.isSubmergedInWater()
				&& !player.isHoldingOntoLadder()
				&& !player.isInLava()
				&& !player.isGliding()
				&& !player.noClip
				&& !player.getAbilities().flying
				&& !EntityUtils.isInWeb(player)
				&& !player.hasVehicle()
				&& player.getPose() != EntityPose.SWIMMING;
	}
}
