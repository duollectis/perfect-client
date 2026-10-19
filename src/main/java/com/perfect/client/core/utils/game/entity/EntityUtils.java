package com.perfect.client.core.utils.game.entity;

import com.perfect.client.core.utils.game.ItemUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.mc;

public class EntityUtils {

	private static final Box EMPTY_BOX = new Box(-100.0, -100.0, -100.0, -100.0, -100.0, -100.0);
	public static final double ANTI_FLOOR_OFFSET = 0.000001;

	
	public static int getHurtTime(Entity entity) {
		if (entity instanceof LivingEntity living) {
			return living.hurtTime;
		}
		return 0;
	}

	
	public boolean isPhased(Box box) {
		if (mc.player == null) {
			return false;
		}

		List<BlockPos> intersected = PositionMath.getIntersectedBlocks(mc.player.getBoundingBox());

		for (BlockPos pos : intersected) {
			if (!BlockUtils.isReplaceable(pos)) {
				return true;
			}
		}
		return false;
	}

	
	public static Box getBoxAt(Entity entity, Vec3d pos) {
		return PositionMath.getBoxAt(entity.getBoundingBox(), pos);
	}

	
	public static List<BlockPos> getIntersectedFeet(Entity entity) {
		return getIntersectedFeet(new ArrayList<>(), entity, ANTI_FLOOR_OFFSET);
	}

	
	public static List<BlockPos> getIntersectedFeet(List<BlockPos> list, Entity entity, double offset) {
		if (entity == null) {
			return list;
		}
		Box b = getIntersectionBox(entity, offset);
		return PositionMath.getBoxIntersectedMinimum(list, b);
	}

	
	public static List<BlockPos> getIntersectedHead(Entity entity) {
		return getIntersectedHead(new ArrayList<>(), entity, ANTI_FLOOR_OFFSET);
	}

	
	public static List<BlockPos> getIntersectedHead(List<BlockPos> list, Entity entity, double offset) {
		if (entity == null) {
			return list;
		}
		Box b = getIntersectionBox(entity, offset);
		return PositionMath.getBoxIntersectedMaximum(list, b);
	}

	
	public static List<BlockPos> getIntersected(Entity entity) {
		List<BlockPos> list = new ArrayList<>();

		Box b = getIntersectionBox(entity, ANTI_FLOOR_OFFSET);

		PositionMath.getBoxIntersectedMinimum(list, b);
		PositionMath.getBoxIntersectedMaximum(list, b);

		return list;
	}

	
	private static Box getIntersectionBox(Entity entity, double offset) {
		if (entity == null) {
			return EMPTY_BOX;
		}
		return entity.getBoundingBox().expand(-offset);
	}

	
	public static double getTotalHealth(LivingEntity entity) {
		return entity.getHealth() + entity.getAbsorptionAmount();
	}

	
	public static boolean isInvisible(LivingEntity entity) {
		return entity.hasStatusEffect(StatusEffects.INVISIBILITY) && !hasArmor(entity) && entity
				.getOffHandStack()
				.isEmpty() && entity.getOffHandStack().isEmpty();
	}

	
	public static boolean hasArmor(LivingEntity entity) {
		for (ItemStack stack : getArmor(entity)) {
			if (!stack.isEmpty())
				return true;
		}
		return false;
	}

	
	public static Vec3d getPrevPos(Entity entity) {
		return new Vec3d(entity.lastX, entity.lastY, entity.lastZ);
	}

	
	public static boolean isMoving(Entity entity) {
		return entity.lastX != entity.getX() || entity.lastY != entity.getY() || entity.lastZ != entity.getZ();
	}

	
	public static Vec3d getEyePos(LivingEntity entity) {
		return entity.getPos().add(0.0, entity.getEyeHeight(entity.getPose()), 0.0);
	}

	
	public static boolean isNaked(PlayerEntity player) {
		for (ItemStack stack : EntityUtils.getArmor(player)) {
			if (stack == null || stack.isEmpty()) {
				continue;
			}

			if (ItemUtils.isArmor(stack)) {
				return false;
			}
		}

		return true;
	}

	
	public static boolean isAttackable(EntityType<?> type) {
		return type != EntityType.AREA_EFFECT_CLOUD && type != EntityType.ARROW && type != EntityType.FALLING_BLOCK
				&& type != EntityType.FIREWORK_ROCKET && type != EntityType.ITEM && type != EntityType.LLAMA_SPIT
				&& type != EntityType.SPECTRAL_ARROW && type != EntityType.ENDER_PEARL
				&& type != EntityType.EXPERIENCE_BOTTLE && type != EntityType.SPLASH_POTION
				&& type != EntityType.LINGERING_POTION && type != EntityType.TRIDENT
				&& type != EntityType.LIGHTNING_BOLT && type != EntityType.FISHING_BOBBER
				&& type != EntityType.EXPERIENCE_ORB && type != EntityType.EGG;
	}

	
	public static GameMode getGameMode(PlayerEntity player) {
		if (player == null || mc.getNetworkHandler() == null) {
			return null;
		}
		PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
		if (playerListEntry == null) {
			return null;
		}
		return playerListEntry.getGameMode();
	}

	
	public static boolean isCreative(Entity entity) {
		if (!(entity instanceof PlayerEntity player)) {
			return false;
		}
		return EntityUtils.getGameMode(player) == GameMode.CREATIVE;
	}

	
	public static ItemStack[] getArmor(LivingEntity entity) {
		return new ItemStack[]{
				entity.getEquippedStack(EquipmentSlot.FEET),
				entity.getEquippedStack(EquipmentSlot.LEGS),
				entity.getEquippedStack(EquipmentSlot.CHEST),
				entity.getEquippedStack(EquipmentSlot.HEAD),
				};
	}

	
	public static Vec3d getVelocity(Entity entity) {
		if (entity == null) {
			return Vec3d.ZERO;
		}
		return new Vec3d(entity.getX() - entity.lastX, entity.getY() - entity.lastY, entity.getZ() - entity.lastZ);
	}

	
	public static boolean isInWeb(Entity entity) {
		return BlockUtils.isInWeb(entity.getBoundingBox());
	}

	
	public static int getPing(LivingEntity entity) {
		if (mc.getNetworkHandler() == null) {
			return 0;
		}

		PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());

		if (playerListEntry == null) {
			return 0;
		}

		return playerListEntry.getLatency();
	}

	
	public static boolean isExist(Entity entity) {
		return isExist(entity.getId());
	}

	
	public static boolean isExist(int id) {
		if (mc.world == null) {
			return false;
		}
		return mc.world.getEntityById(id) != null;
	}

	/**
	 * Part from {@link Entity#updateMovementInFluid}
	 */
	public static boolean calculateFluidCollision(TagKey<Fluid> tag, Box box) {
		if (mc.world == null) {
			return false;
		}

		box = box.contract(0.001D);

		int minX = MathHelper.floor(box.minX);
		int maxX = MathHelper.ceil(box.maxX);
		int minY = MathHelper.floor(box.minY);
		int maxY = MathHelper.ceil(box.maxY);
		int minZ = MathHelper.floor(box.minZ);
		int maxZ = MathHelper.ceil(box.maxZ);

		BlockPos.Mutable pos = new BlockPos.Mutable();

		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {

					pos.set(x, y, z);

					FluidState fluidState = mc.world.getFluidState(pos);

					if (fluidState.isIn(tag) && y + fluidState.getHeight(mc.world, pos) >= box.minY) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
