package com.perfect.client.functional.function.combat.autoCrystal.handlers;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.helper.handler.FunctionHandler;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.math.PositionMath;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystal;
import com.perfect.client.functional.function.combat.autoCrystal.AutoCrystalConfig;
import com.perfect.client.functional.function.combat.autoCrystal.data.BreakInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.PlaceInfo;
import com.perfect.client.functional.function.combat.autoCrystal.data.State;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class EntitySpawnHandler extends FunctionHandler<AutoCrystal, AutoCrystalConfig> {

	public EntitySpawnHandler(AutoCrystal function, AutoCrystalConfig config) {
		super(function, config);
	}

	@Override
	public void onPrePacketReceive(PacketEvent event) {
		EndCrystalEntity crystal = getCrystalIfPresented(event.getPacket());

		if (crystal == null) {
			return;
		}

		executeTask(() -> doInstantBreak(crystal));
	}

	
	private void doInstantBreak(EndCrystalEntity crystal) {
		if (!function.isEnabled() || function.getTargets().isEmpty() || function.shouldPause()) {
			return;
		}

		findInstantBreak(crystal);

		if (isInfoFoundForCrystal(crystal)) {
			findInstantPlace();
			attack(crystal);
			doInstantPlace();
		}
	}

	
	private void findInstantBreak(EndCrystalEntity crystal) {
		if (shouldRecalculateBreak(crystal)) {
			State state = function.createState();
			BreakInfo breakInfo = function.getBreakHelper().getBreak(state, crystal);

			function.setBreakInfo(breakInfo);
		}
		else {
			BreakInfo breakInfo = new BreakInfo();
			breakInfo.setCrystal(crystal);
			breakInfo.setFacePlace(function.getPlaceInfo().isFacePlace());

			function.setBreakInfo(breakInfo);
		}
	}

	private void findInstantPlace() {
		if (config.getSequential().get()) {
			function.setPlaceInfo(function.findPlaceInfo(false));
		}
	}

	private void doInstantPlace() {
		PlaceInfo placeInfo = function.getPlaceInfo();

		if (config.getSequential().get() && placeInfo != null) {
			function.getPlaceHelper().placeCrystal(placeInfo);
			function.setPlacedOnSpawn(true);
		}
	}

	private void attack(EndCrystalEntity crystal) {
		function.setBrokenOnSpawn(crystal);
		function.getBreakHelper().attackCrystal(function.getBreakInfo());
	}

	private boolean isInfoFoundForCrystal(EndCrystalEntity crystal) {
		BreakInfo breakInfo = function.getBreakInfo();
		if (breakInfo == null) {
			return false;
		}

		return breakInfo.getCrystal().equals(crystal);
	}

	private boolean shouldRecalculateBreak(EndCrystalEntity crystal) {
		if (function.getPlaceInfo() == null) {
			return true;
		}

		BlockPos validPos = function.getPlaceInfo().getPos().up();
		return !crystal.getBlockPos().equals(validPos);
	}

	private EndCrystalEntity getCrystalIfPresented(Packet<?> p) {
		if (p instanceof EntitySpawnS2CPacket packet && isGoodPacket(packet)) {
			return createCrystalByPacket(packet);
		}

		return null;
	}

	private boolean isGoodPacket(EntitySpawnS2CPacket packet) {
		if (packet.getEntityType() != EntityType.END_CRYSTAL || mc.player == null) {
			return false;
		}

		if (function.isStrictTiming()) {
			return false;
		}

		Vec3d packetPos = PacketUtils.getPos(packet);
		boolean raycast = !hasLineOfSight(mc.player.getEyePos(), packetPos);

		if (config.getWallsBreakRange().isEnabled() && raycast) {
			return !PositionMath.isOutOfRange(packetPos, config.getWallsBreakRange().get());
		}

		return !PositionMath.isOutOfRange(packetPos, config.getBreakRange().get());
	}

	private EndCrystalEntity createCrystalByPacket(EntitySpawnS2CPacket packet) {
		EndCrystalEntity crystal = new EndCrystalEntity(mc.world, 0.0, 0.0, 0.0);
		crystal.onSpawnPacket(packet);
		return crystal;
	}

	
	public boolean hasLineOfSight(Vec3d start, Vec3d end) {
		assert mc.world != null;
		assert mc.player != null;

		RaycastContext context = new RaycastContext(
				start,
				end,
				RaycastContext.ShapeType.COLLIDER,
				RaycastContext.FluidHandling.NONE,
				mc.player
		);

		BlockHitResult result = mc.world.raycast(context);

		return result.getType() == HitResult.Type.MISS;
	}

	
	private void executeTask(Runnable runnable) {
		int minAge = config.getCrystalAge().get() + function.getAwaitMillis();

		if (minAge > 0) {
			Managers.ASYNC.run(minAge, runnable);
		}
		else {
			runnable.run();
		}
	}
}
