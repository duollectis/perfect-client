package com.perfect.client.functional.function.misc.enderChestFarm;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.interact.HitResultUtils;
import com.perfect.client.core.utils.functions.interact.InteractUtils;
import com.perfect.client.core.utils.functions.interact.SupportUtils;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.functions.inventory.SwapInfo;
import com.perfect.client.core.utils.functions.inventory.SwapUtils;
import com.perfect.client.core.utils.functions.mining.MiningPriority;
import com.perfect.client.core.utils.functions.target.TargetUtils;
import com.perfect.client.core.utils.functions.trap.TrapType;
import com.perfect.client.core.utils.functions.trap.TrapUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.perfect.client.manager.system.FunctionManager.packetMine;


public class EnderChestFarm extends Function {

	private final EnderChestFarmConfig config = new EnderChestFarmConfig(this);

	private BlockPos chestPos;
	private List<BlockPos> protect;

	public EnderChestFarm() {
		super(
				"ender-chest-farm",
				"Ender Chest Farm",
				Category.MISC,
				"Auto Ender Chest",
				"Auto EC Farmer"
		);
	}

	@Override
	public void onEnable() {
		protect = new ArrayList<>();
		chestPos = null;

		if (mc.world == null || mc.player == null) {
			disable();
			return;
		}

		boolean found = updatePositions();
		if (!found) {
			error("Position not found");
			disable();
		}
	}

	@Override
	public void onDisable() {
		protect = null;
		chestPos = null;
	}

	@Override
	public void onHandleInput() {
		if (mc.world == null || mc.player == null)
			return;
		if (chestPos == null)
			return;

		if (PositionMath.isOutOfRange(chestPos, config.getRange().get())) {
			error("Position too far");
			disable();
			return;
		}

		boolean placed = Managers.PENDING.contains(chestPos)
				|| !BlockUtils.isReplaceable(chestPos)
				|| hasEntity(chestPos);

		if (!placed) {
			BlockHitResult hitResult = HitResultUtils.getPlaceResult(chestPos);

			if (hitResult == null) {
				updatePositions();
				return;
			}

			SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), Items.ENDER_CHEST);

			InteractUtils.placeBlock(
					hitResult,
					InvUtils.find(Items.ENDER_CHEST),
					config.getRotate().get(),
					InteractUtils.Argument.NO_ITEM,
					swapInfo.isOffhand() ? InteractUtils.Argument.OFF_HAND : null
			);

			swapInfo.swapBack();
		}

		SupportUtils.addSupport(protect, config.getSupport().get(), config.getSupportLength().get());

		for (BlockPos pos : protect) {
			if (!BlockUtils.isReplaceable(pos) || Managers.PENDING.contains(pos) || BlockUtils.isOutOfBorder(pos)
					|| hasEntity(pos)) {
				continue;
			}

			if (config.getBreakCrystals().get()) {
				for (Entity entity : BlockUtils.getEntities(pos)) {
					if (!(entity instanceof EndCrystalEntity crystal)) {
						continue;
					}

					if (config.getBreakRotate().get()) {
						Vec3d vec3d = PositionMath.closestVec3d(
								mc.player.getEyePos(),
								crystal.getBoundingBox().expand(-0.1)
						);

						sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
								RotationUtils.getYaw(vec3d),
								RotationUtils.getPitch(vec3d),
								mc.player.isOnGround(),
								mc.player.horizontalCollision
						));
					}

					sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
					sendPacket(new HandSwingC2SPacket(
							mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL ? Hand.OFF_HAND : Hand.MAIN_HAND
					));

					Managers.CRYSTAL.onAttackCrystal(crystal);
					break;
				}
			}

			SwapInfo swapInfo = SwapUtils.swap(config.getSwap().get(), Items.ENDER_CHEST);

			InteractUtils.placeBlock(
					pos,
					Items.OBSIDIAN,
					config.getRotate().get(),
					InteractUtils.Argument.NO_ITEM,
					swapInfo.isOffhand() ? InteractUtils.Argument.OFF_HAND : null
			);

			swapInfo.swapBack();

			break;
		}

		if (config.getMine().get()) {
			packetMine.startBreaking(chestPos, mc.player.getFacing(), MiningPriority.ENDER_CHEST_FARM, getId());
		}
	}

	private boolean updatePositions() {
		List<BlockPos> surround = TrapUtils.getTrap(mc.player, TrapType.SURROUND, false);
		List<BlockPos> faceTrap = TrapUtils.getTrap(mc.player, TrapType.FACE, false);

		boolean surrounded = TrapUtils.isTrapped(
				surround,
				pos -> !BlockUtils.isReplaceable(pos) && !BlockUtils.isOf(pos, Blocks.ENDER_CHEST)
		);

		if (surrounded && TrapUtils.isTrapped(
				faceTrap,
				pos -> !BlockUtils.isReplaceable(pos) && !BlockUtils.isOf(pos, Blocks.ENDER_CHEST)
		)) {
			return false;
		}

		List<BlockPos> possiblePositions = surrounded ? faceTrap : surround;
		possiblePositions =
				new ArrayList<>(possiblePositions.stream().filter(pos -> !BlockUtils.isOutOfBorder(pos) && (
						BlockUtils.isReplaceable(pos) || BlockUtils.isOf(pos, Blocks.ENDER_CHEST)
				) && !hasEntity(pos) && (
						HitResultUtils.getPlaceResult(pos) != null || BlockUtils.isOf(pos, Blocks.ENDER_CHEST)
				)).toList());

		if (possiblePositions.isEmpty()) {
			return false;
		}

		possiblePositions.sort(Comparator.comparing(pos -> {
			double best = 0;
			for (Entity entity : TargetUtils.getTargetsInRange(new Box(pos).expand(2), 2, false, null)) {
				double distance = PositionMath.squareDistance(entity.getPos(), Vec3d.ofBottomCenter(pos.up()));
				if (distance > best) {
					best = distance;
				}
			}
			return -best;
		}));

		chestPos = possiblePositions.getFirst();

		if (config.getSideProtect().get()) {
			Direction.Type.HORIZONTAL.forEach(dir -> protect.add(chestPos.offset(dir)));
		}
		if (config.getTopProtect().get()) {
			protect.add(chestPos.up());
		}

		return true;
	}

	private boolean hasEntity(BlockPos pos) {
		return BlockUtils.hasEntity(
				pos,
				entity -> entity.intersectionChecked && (!config.getBreakCrystals().get()
						|| !(entity instanceof EndCrystalEntity)
				)
		);
	}
}
