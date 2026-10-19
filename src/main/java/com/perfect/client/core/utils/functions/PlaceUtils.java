package com.perfect.client.core.utils.functions;

import com.perfect.client.core.functional.function.placeFunction.data.PlaceInfo;
import com.perfect.client.core.functional.setting.impl.enums.EnumSetting;
import com.perfect.client.core.functional.setting.impl.list.BlocksSetting;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.inventory.*;
import com.perfect.client.core.utils.game.PacketUtils;
import com.perfect.client.core.utils.game.entity.PlayerUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;

public class PlaceUtils {

	private static final MinecraftClient mc = MinecraftClient.getInstance();

	public static EndCrystalEntity getCrystalByBlock(BlockPos pos) {
		List<Entity> entities = BlockUtils.getEntities(pos, entity -> entity instanceof EndCrystalEntity);

		if (!entities.isEmpty() && entities.getFirst() instanceof EndCrystalEntity crystal) {
			return crystal;
		}

		return null;
	}

	public static SwapInfo getSwapInfo(EnumSetting<Swap> swap, BlocksSetting blocks) {
		FindItemResult fir = null;
		boolean found = false;

		if (blocks.get().contains(Blocks.OBSIDIAN)) {
			FindItemResult obsidian;

			if (swap.is(Swap.HOTBAR)) {
				obsidian = InvUtils.findInHotbar(Items.OBSIDIAN);
			}
			else {
				obsidian = InvUtils.find(Items.OBSIDIAN);
			}

			if (obsidian.found()) {
				found = true;
				fir = obsidian;
			}
		}

		if (!found) {
			FindItemResult block;
			if (swap.is(Swap.HOTBAR)) {
				block = InvUtils.findInHotbar(stack -> stack.getItem() instanceof BlockItem blockItem && blocks
						.get()
						.contains(blockItem.getBlock()));
			}
			else {
				block = InvUtils.find(stack -> stack.getItem() instanceof BlockItem blockItem && blocks
						.get()
						.contains(blockItem.getBlock()));
			}

			if (block.found()) {
				found = true;
				fir = block;
			}
		}

		if (!found) {
			return SwapInfo.NOT_FOUND;
		}

		return SwapUtils.swap(swap.get(), fir);
	}

	public static void packetRotate(Vec3d vec3d) {
		if (mc.player == null) {
			return;
		}

		float yaw = RotationUtils.getYaw(vec3d);
		float pitch = RotationUtils.getPitch(vec3d);

		if (yaw != Managers.ROTATION.getSentYaw() || pitch != Managers.ROTATION.getSentPitch()) {
			double fov = PlayerUtils.getFov(vec3d, Managers.ROTATION.getSentYaw(), Managers.ROTATION.getSentPitch());

			if (fov > clientConfig.getRotationGap()) {
				PacketUtils.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
						yaw,
						pitch,
						mc.player.isOnGround(),
						mc.player.horizontalCollision
				));
			}
		}
	}

	public static void attackCrystal(EndCrystalEntity crystal) {
		if (mc.getNetworkHandler() == null || mc.player == null || mc.world == null) {
			return;
		}

		if (crystal.isAlive()) {
			PacketUtils.sendPacket(PlayerInteractEntityC2SPacket.attack(crystal, mc.player.isSneaking()));
			PacketUtils.sendPacket(new HandSwingC2SPacket(Hand.OFF_HAND));
		}
	}

	public static PlaceInfo getNextPlaceInfo(
			List<PlaceInfo> places,
			EndCrystalEntity crystal,
			List<PlaceInfo> wasPlaced
	) {

		PlaceInfo bestInfo = null;
		double bestFov = Double.MAX_VALUE;
		boolean bestProtect = false;

		for (PlaceInfo info : new ArrayList<>(places)) {
			if (!BlockUtils.isReplaceable(info.getPosition()) || (wasPlaced != null && wasPlaced.contains(info))) {
				continue;
			}

			boolean isProtect = crystal != null && PositionMath.intersectsOrCollides(
					crystal.getBoundingBox(),
					new Box(info.getPosition())
			);

			if (bestProtect && !isProtect)
				continue;

			double fov = PlayerUtils.getFov(
					info.getHitResult().getPos(),
					Managers.ROTATION.getSentYaw(),
					Managers.ROTATION.getSentPitch()
			);

			if (!bestProtect && isProtect) {
				bestProtect = true;
				bestInfo = info;
				continue;
			}

			if (fov < bestFov) {
				bestInfo = info;
				bestFov = fov;
			}
		}

		return bestInfo;
	}
}
