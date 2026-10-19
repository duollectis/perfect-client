package com.perfect.client.functional.function.combat.maceKill;

import com.perfect.client.core.event.events.network.PacketEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.inject.mixinInterface.IPlayerInteractEntityC2SPacket;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MaceKill extends Function {

	private final MaceKillConfig config = new MaceKillConfig(this);
	private Vec3d prevPos;

	public MaceKill() {
		super("mace-kill", "Mace Kill", Category.COMBAT);
	}

	@Override
	public void onPrePacketSend(PacketEvent event) {
		if (mc.player == null || mc.world == null) {
			return;
		}

		int blocks = getMaxHeightAbovePlayer();
		if (shouldCancelAttack(event.getPacket(), blocks)) {
			return;
		}

		prevPos = mc.player.getPos();
		Entity vehicle = mc.player.getVehicle();
		boolean hasVehicle = vehicle != null;

		int packetsRequired = (int) Math.ceil(Math.abs(blocks / 10.0));
		if (packetsRequired > 20) packetsRequired = 1;

		int spamCount = (blocks <= 22) ? 4 : (packetsRequired - 1);

		for (int i = 0; i < spamCount; i++) {
			if (hasVehicle) {
				mc.player.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(vehicle));
			} else {
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(false, mc.player.horizontalCollision));
			}
		}

		double limit = Math.max(blocks, 22);

		if (hasVehicle) {
			double maxHeight = Math.min(vehicle.getY() + limit, vehicle.getY() + blocks);
			doVehicleTeleports(vehicle, maxHeight, blocks);
		} else {
			double heightY = Math.min(mc.player.getY() + limit, mc.player.getY() + blocks);
			doPlayerTeleports(heightY);
		}
	}

	private boolean shouldCancelAttack(Packet<?> packet, int blocks) {
		assert mc.player != null;

		if (mc.player.getMainHandStack().getItem() != Items.MACE) {
			return true;
		}

		if (!(packet instanceof IPlayerInteractEntityC2SPacket interactPacket) || !interactPacket.perfect$isAttack()) {
			return true;
		}

		if (!(interactPacket.perfect$getEntity() instanceof LivingEntity targetEntity)) {
			return true;
		}

		if (config.getPauseOnBlocked().get() && (targetEntity.isBlocking() || targetEntity.isInvulnerable() || targetEntity.isInCreativeMode())) {
			return true;
		}

		if (blocks <= 0) {
			return true;
		}

		BlockPos playerPos = mc.player.getBlockPos();
		return !isSafeBlock(playerPos.add(0, blocks, 0)) || !isSafeBlock(playerPos.add(0, blocks + 1, 0));
	}

	private void doPlayerTeleports(double height) {
		assert mc.player != null;

		boolean preventDeath = config.getPreventDeath().get();
		double targetY = preventDeath ? prevPos.getY() + 0.25 : prevPos.getY();
		boolean collision = mc.player.horizontalCollision;

		PlayerMoveC2SPacket teleportPacket = new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), height, mc.player.getZ(), false, collision);
		PlayerMoveC2SPacket backPacket = new PlayerMoveC2SPacket.PositionAndOnGround(prevPos.getX(), targetY, prevPos.getZ(), false, collision);

		mc.player.networkHandler.sendPacket(teleportPacket);
		mc.player.networkHandler.sendPacket(backPacket);

		if (preventDeath) {
			mc.player.setVelocity(mc.player.getVelocity().x, 0.1, mc.player.getVelocity().z);
			mc.player.fallDistance = 0;
		}
	}

	private void doVehicleTeleports(Entity vehicle, double height, int blocks) {
		assert mc.player != null;

		vehicle.setPosition(vehicle.getX(), height + blocks, vehicle.getZ());
		mc.player.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(vehicle));

		vehicle.setPosition(prevPos);
		mc.player.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(vehicle));
	}

	private int getMaxHeightAbovePlayer() {
		assert mc.player != null;

		BlockPos playerPos = mc.player.getBlockPos();
		int maxHeight = playerPos.getY() + config.getFallHeight().getInt();

		for (int i = maxHeight; i > playerPos.getY(); i--) {
			BlockPos up1 = new BlockPos(playerPos.getX(), i, playerPos.getZ());

			if (isSafeBlock(up1) && isSafeBlock(up1.up(1))) {
				return i - playerPos.getY();
			}
		}

		return 0;
	}

	private boolean isSafeBlock(BlockPos pos) {
		assert mc.world != null;

		BlockState state = mc.world.getBlockState(pos);

		return state.isReplaceable()
				&& mc.world.getFluidState(pos).isEmpty()
				&& !state.isOf(Blocks.POWDER_SNOW);
	}
}
