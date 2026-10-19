package com.perfect.client.core.utils.functions.interact;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.FindItemResult;
import com.perfect.client.core.utils.functions.inventory.InvUtils;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;

public class InteractUtils {

	public static boolean placeBlock(BlockPos pos, Item item, Rotate rotateMode, Argument... args) {
		return placeBlock(pos, InvUtils.find(item), rotateMode, args);
	}

	public static boolean placeBlock(BlockPos pos, FindItemResult fir, Rotate rotateMode, Argument... args) {
		return placeBlock(HitResultUtils.getPlaceResult(pos), fir, rotateMode, args);
	}

	
	public static boolean placeBlock(BlockHitResult result, FindItemResult fir, Rotate rotateMode, Argument... args) {
		List<Argument> arguments = Arrays.asList(args);
		if (mc.player == null || (!arguments.contains(Argument.NO_ITEM) && !fir.found())) {
			return false;
		}
		if (result == null) {
			return false;
		}

		if (rotateMode != Rotate.NONE) {
			float yaw = RotationUtils.getYaw(result.getPos());
			float pitch = RotationUtils.getPitch(result.getPos());

			boolean needRotate = true;

			if (clientConfig.getRotationGap() != 0) {
				needRotate = PlayerUtils.getFov(
						result.getPos(),
						Managers.ROTATION.getSentYaw(),
						Managers.ROTATION.getSentPitch()
				) > clientConfig.getRotationGap();
			}

			if (needRotate) {
				switch (rotateMode) {
					case VANILLA -> {
						new Rotation().rotate(yaw, pitch, () -> placeBlockIternal(result, fir, args));
						return true;
					}
					case PACKET -> Managers.ROTATION.packetRotate(yaw, pitch);
					case GRIM -> PacketUtils.sendPacket(new PlayerMoveC2SPacket.Full(
							mc.player.getX(),
							mc.player.getY(),
							mc.player.getZ(),
							yaw,
							pitch,
							mc.player.isOnGround(),
							mc.player.horizontalCollision
					));
				}
			}
		}

		placeBlockIternal(result, fir, args);

		Managers.PENDING.add(result, getPendingState(fir));

		return true;
	}

	private static BlockState getPendingState(FindItemResult fir) {
		if (fir == null || fir.stack() == null) {
			return Blocks.AIR.getDefaultState();
		}

		if (fir.stack().getItem() instanceof BlockItem blockItem) {
			return blockItem.getBlock().getDefaultState();
		}

		return Blocks.AIR.getDefaultState();
	}

	
	public static void placeBlockIternal(BlockHitResult result, FindItemResult fir, Argument... args) {
		if (result == null
				|| mc.player == null
				|| mc.world == null
				|| mc.player.networkHandler == null
				|| mc.interactionManager == null
		) {
			return;
		}

		List<Argument> arguments = Arrays.asList(args);
		boolean noItem = arguments.contains(Argument.NO_ITEM);
		if (!noItem && !fir.found()) {
			return;
		}

		Hand hand = Hand.MAIN_HAND;

		if (!noItem) {
			if (fir.isOffhand()) {
				hand = Hand.OFF_HAND;
			}
			if (!fir.isMain() && !fir.isOffhand() && mc.player.getInventory().getSelectedSlot() != fir.slot()) {
				InvUtils.setSlot(fir.slot(), true);
			}
		}

		if (arguments.contains(Argument.OFF_HAND)) {
			hand = Hand.OFF_HAND;
		}

		boolean shifting = BlockUtils.needShiftInteract(result.getBlockPos());
		if (!shifting && Managers.PENDING.contains(result.getBlockPos())) {
			shifting = BlockUtils.needShiftInteract(Managers.PENDING.getState(result.getBlockPos()));
		}

		if (shifting) {
			PacketUtils.sendSneaking(true);
		}

		if (!arguments.contains(Argument.VANILLA_PLACE)) {
			Hand _hand = hand;
			PacketUtils.sendSequencedPacket(creator -> new PlayerInteractBlockC2SPacket(_hand, result, creator));
		}
		else {
			mc.interactionManager.interactBlock(mc.player, hand, result);
		}

		PacketUtils.sendPacket(new HandSwingC2SPacket(hand));

		if (shifting) {
			PacketUtils.sendSneaking(false);
		}

		if (!noItem) {
			InvUtils.swapBack();
		}
	}

	
	public static void simplePlace(BlockHitResult result, Hand hand, boolean swing) {
		if (result == null || mc.player == null || mc.world == null) {
			return;
		}

		boolean shifting = BlockUtils.needShiftInteract(result.getBlockPos());
		if (!shifting && Managers.PENDING.contains(result.getBlockPos())) {
			shifting = BlockUtils.needShiftInteract(Managers.PENDING.getState(result.getBlockPos()));
		}

		if (shifting) {
			PacketUtils.sendSneaking(true);
		}

		PacketUtils.sendSequencedPacket(creator -> new PlayerInteractBlockC2SPacket(hand, result, creator));

		if (swing) {
			mc.player.swingHand(hand);
		}
		else {
			PacketUtils.sendPacket(new HandSwingC2SPacket(hand));
		}

		if (shifting) {
			PacketUtils.sendSneaking(false);
		}
	}

	public enum Argument {
		NO_ITEM,
		OFF_HAND,
		VANILLA_PLACE;
	}
}
