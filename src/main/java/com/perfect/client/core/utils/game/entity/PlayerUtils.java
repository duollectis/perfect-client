package com.perfect.client.core.utils.game.entity;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.functions.MovementUtils;
import com.perfect.client.core.utils.game.ItemUtils;
import com.perfect.client.core.utils.game.rotation.RotationUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.text.StringUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.manager.system.FunctionManager.autoExp;
import static com.perfect.client.manager.system.FunctionManager.fakeLag;

public class PlayerUtils {

	@Getter
	@Setter
	private static boolean tickUpdateLock;

	public static double getSpeedMultiplier() {
		if (mc.player == null) {
			return 0;
		}

		double defaultSpeed = 1f;

		if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
			int amplifier = mc.player.getStatusEffect(StatusEffects.SPEED).getAmplifier();
			defaultSpeed *= 1d + 0.2d * (double) (amplifier + 1);
		}

		if (mc.player.hasStatusEffect(StatusEffects.SLOWNESS)) {
			int amplifier = mc.player.getStatusEffect(StatusEffects.SLOWNESS).getAmplifier();
			defaultSpeed /= 1d + 0.2d * (double) (amplifier + 1);
		}

		return defaultSpeed;
	}

	
	public static boolean isCrawling() {
		if (mc.player == null) {
			return false;
		}
		return mc.player.getPose() == EntityPose.SWIMMING;
	}

	
	public static boolean isShifting() {
		if (mc.player == null) {
			return false;
		}
		return Managers.NETWORK.isShifting() || mc.player.getPose() == EntityPose.CROUCHING;
	}

	
	public static boolean isPhased() {
		for (BlockPos pos : getIntersected()) {
			if (BlockUtils.isCollidable(pos) && isCollides(pos)) {
				return true;
			}
		}

		return false;
	}

	
	public static boolean isDoublePhased() {
		List<BlockPos> intersectedCorners = getIntersected();

		if (intersectedCorners.isEmpty()) {
			return false;
		}

		for (BlockPos pos : intersectedCorners) {
			if (!BlockUtils.isCollidable(pos) || !isCollides(pos)) {
				return false;
			}
		}

		return true;
	}

	
	private static boolean isCollides(BlockPos pos) {
		if (mc.player == null) {
			return false;
		}
		VoxelShape shape = BlockUtils.getState(pos).getOutlineShape(mc.world, pos);
		if (shape.isEmpty()) {
			return false;
		}
		return shape.getBoundingBox().offset(pos).intersects(mc.player.getBoundingBox());
	}

	
	public static List<BlockPos> getIntersected() {
		return EntityUtils.getIntersected(mc.player);
	}

	
	public static boolean isInWeb() {
		if (mc.player == null) {
			return false;
		}
		return EntityUtils.isInWeb(mc.player);
	}

	
	public static boolean isElytraFlying() {
		if (mc.player == null) {
			return false;
		}
		return canOpenElytra(false) && mc.player.isGliding();
	}

	
	public static boolean canOpenElytra(boolean checkFallFlying) {
		if (mc.player == null) {
			return false;
		}
		if (mc.player.isOnGround()) {
			return false;
		}
		if (checkFallFlying && mc.player.isGliding()) {
			return false;
		}
		return canElytraTakeoff(false);
	}

	
	public static boolean canElytraTakeoff(boolean ignoreElytraCheck) {
		if (mc.player == null) {
			return false;
		}
		if (mc.player.getAbilities() != null && mc.player.getAbilities().flying) {
			return false;
		}
		if (mc.player.isTouchingWater()) {
			return false;
		}
		if (mc.player.hasStatusEffect(StatusEffects.LEVITATION)) {
			return false;
		}
		if (mc.player.hasVehicle()) {
			return false;
		}
		if (mc.player.isClimbing()) {
			return false;
		}

		if (ignoreElytraCheck) {
			return true;
		}

		ItemStack itemStack = mc.player.getEquippedStack(EquipmentSlot.CHEST);
		return itemStack.isOf(Items.ELYTRA) && !ItemUtils.isBroken(itemStack);
	}

	
	public static int getEffectAmplifier(RegistryEntry<StatusEffect> registry) {
		if (mc.player == null) {
			return 0;
		}
		StatusEffectInstance effect = mc.player.getStatusEffect(registry);
		if (effect == null) {
			return 0;
		}
		return effect.getAmplifier();
	}

	
	public static void setStep(double value) {
		if (mc.player == null) {
			return;
		}

		EntityAttributeInstance step = mc.player.getAttributeInstance(EntityAttributes.STEP_HEIGHT);

		if (step != null) {
			step.setBaseValue(value);
		}
	}

	
	public static double getFov(Vec3d vec3d, float yaw, float pitch) {
		if (mc.player == null) {
			return 0;
		}

		float yawRange = Math.abs(RotationUtils.getYaw(vec3d) - yaw);
		float pitchRange = Math.abs(RotationUtils.getPitch(vec3d) - pitch);
		return yawRange * yawRange + pitchRange * pitchRange;
	}

	// Я эт для суррчика давно писал не удалять
	
	public static Vec3d closestCenter(BlockPos pos) {
		if (mc.player == null) {
			return Vec3d.ZERO;
		}

		double s = 0.6000001;
		Box box = new Box(mc.player.getBlockPos()).offset(s / 2, 0, s / 2).shrink(s, 0, s);

		double x = MathHelper.clamp(mc.player.getX(), box.minX, box.maxX);
		double z = MathHelper.clamp(mc.player.getZ(), box.minZ, box.maxZ);
		return new Vec3d(x, mc.player.getY(), z);
	}

	
	public static boolean shouldPause(boolean ifEating) {
		return shouldPause(ifEating, false, false);
	}

	
	public static boolean shouldPause(boolean ifEating, boolean fakeLag) {
		return shouldPause(ifEating, false, fakeLag);
	}

	
	public static boolean shouldPause(boolean ifEating, boolean autoExp, boolean blink) {
		return shouldPause(ifEating, false, autoExp, blink);
	}

	
	public static boolean shouldPause(boolean ifEating, boolean ifShifting, boolean ifAutoExp, boolean ifBlink) {
		if (mc.player == null) {
			return true;
		}

		if (ifBlink && fakeLag.isEnabled()) {
			return true;
		}

		if (ifAutoExp && autoExp.isThrowing()) {
			return true;
		}

		if (ifShifting && mc.player.isSneaking()) {
			return true;
		}

		if (ifEating && (mc.player.isUsingItem() && (
				mc.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD) || mc.player
						.getOffHandStack()
						.getItem()
						.getComponents()
						.contains(DataComponentTypes.FOOD)
		)
		))
			return true;

		return ifEating && (mc.player.isUsingItem() && (mc.player.getMainHandStack().getItem() instanceof PotionItem
				|| mc.player.getOffHandStack().getItem() instanceof PotionItem
		)
		);
	}

	
	public static boolean isMoving() {
		if (mc.player == null) {
			return false;
		}
		return EntityUtils.isMoving(mc.player);
	}

	
	public static float squaredDistanceFromEyes(@NotNull Vec3d targetPos) {
		if (mc.player == null) {
			return 0.0f;
		}

		double dx = targetPos.x - mc.player.getX();
		double dy = targetPos.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
		double dz = targetPos.z - mc.player.getZ();

		return (float) (dx * dx + dy * dy + dz * dz);
	}

	
	public static Vec3d getSpeedInBPS() {
		return getVelocity().multiply(20.0);
	}

	
	public static Vec3d getVelocity() {
		return EntityUtils.getVelocity(mc.player);
	}

	
	public static boolean isInputMoving() {
		if (mc.player == null) {
			return false;
		}
		return MovementUtils.getMovementForward() != 0 || MovementUtils.getMovementSideways() != 0;
	}

	
	public static boolean isSurvival() {
		if (mc.player == null) {
			return false;
		}

		if (mc.player.playerListEntry == null) {
			return false;
		}

		return mc.player.playerListEntry.getGameMode() == GameMode.SURVIVAL;
	}

	public static String getFormattedCoordinates() {
		if (mc.world == null || mc.player == null) {
			return "0.0, 0.0, 0.0";
		}

		Vec3d pos = mc.player.getPos();

		String x = StringUtils.formatDoubles(pos.getX(), 1);
		String y = StringUtils.formatDoubles(pos.getY(), 1);
		String z = StringUtils.formatDoubles(pos.getZ(), 1);

		return x + ", " + y + ", " + z;
	}
}
