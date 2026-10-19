package com.perfect.client.core.utils.functions.mining;

import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.functions.inventory.EnchantmentUtils;
import com.perfect.client.core.utils.game.world.BlockUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;

public class MiningUtils {

	public static void render(
			MatrixStack matrices,
			BlockPos pos,
			float lerpProgress,
			double animation,
			Color lineColor,
			Color sideColor,
			Cosmetics cosmetics
	) {
		render(matrices, pos, lerpProgress, animation, new MultiColor(lineColor), new MultiColor(sideColor), cosmetics);
	}

	public static void render(
			MatrixStack matrices,
			BlockPos pos,
			float lerpProgress,
			double animation,
			MultiColor lineColor,
			MultiColor sideColor,
			Cosmetics cosmetics
	) {
		double p = 1 - MathHelper.clamp(lerpProgress, 0f, 1f);
		p = 1 - Math.pow(p, animation);

		BoxRenderer.drawBox(
				matrices,
				getRenderBox(pos, p / 2f),
				lineColor,
				sideColor,
				cosmetics
		);
	}

	public static float getRenderProgress(float prevProgress, float progress, boolean smooth) {
		if (smooth) {
			return MathUtils.interpolateFloat(
					prevProgress,
					progress,
					RenderUtils.getTickDelta()
			);
		}
		else {
			return progress;
		}
	}

	public static boolean canBreak(BlockPos pos) {
		if (mc.world == null) {
			return false;
		}
		return mc.world.getBlockState(pos).getBlock().getHardness() != -1;
	}

	public static float getBlockStrength(
			PlayerEntity player,
			boolean onGround,
			ItemStack stack,
			BlockState state,
			float hardness
	) {
		if (hardness < 0 || player == null) {
			return 0f;
		}
		return getDigSpeed(stack, state, player, onGround) / hardness / 30f;
	}

	public static float getDigSpeed(
			ItemStack stack,
			BlockState state,
			PlayerEntity player,
			boolean onGround
	) {
		if (player == null) {
			return 0f;
		}

		float digSpeed = getDestroySpeed(stack, state, player);

		if (digSpeed > 1 && stack != null) {
			int efficiencyModifier = EnchantmentUtils.getLevel(stack, Enchantments.EFFICIENCY);
			if (efficiencyModifier > 0 && !stack.isEmpty()) {
				digSpeed += (float) (StrictMath.pow(efficiencyModifier, 2) + 1);
			}
		}

		if (player.hasStatusEffect(StatusEffects.HASTE)) {
			StatusEffectInstance HASTE = player.getStatusEffect(StatusEffects.HASTE);
			if (HASTE != null) {
				digSpeed *= 1 + (HASTE.getAmplifier() + 1) * 0.2F;
			}
		}

		if (player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
			StatusEffectInstance MINING_FATIGUE = player.getStatusEffect(StatusEffects.MINING_FATIGUE);
			if (MINING_FATIGUE != null) {
				digSpeed *= (float) Math.pow(0.3f, MINING_FATIGUE.getAmplifier() + 1);
			}
		}

		if (player.isSubmergedInWater()) {
			EntityAttributeInstance SUBMERGED_MINING_SPEED = player.getAttributeInstance(
					EntityAttributes.SUBMERGED_MINING_SPEED
			);

			if (SUBMERGED_MINING_SPEED != null) {
				digSpeed *= (float) SUBMERGED_MINING_SPEED.getValue();
			}
		}

		if (player == mc.player && !onGround)
			digSpeed /= 5;

		return Math.max(digSpeed, 0);
	}

	public static float getDestroySpeed(ItemStack stack, BlockState state, PlayerEntity player) {
		float destroySpeed = 1;

		if (player == null) {
			player = mc.player;
		}

		if (player == null) {
			return 0;
		}

		if (stack != null && !stack.isEmpty()) {
			destroySpeed *= stack.getMiningSpeedMultiplier(state);
		}

		return destroySpeed;
	}

	public static MultiColor getColor(
			BreakColor colorMode,
			MultiColor staticColor,
			Color startColor,
			Color endColor,
			float progress,
			boolean smoothColor
	) {
		return switch (colorMode) {
			case STATIC -> staticColor;
			case PROGRESS -> getProgressColor(
					startColor,
					endColor,
					smoothColor,
					progress
			);
		};
	}

	public static MultiColor getProgressColor(Color startColor, Color endColor, boolean smooth, float progress) {
		Color col = getProgressColorIternal(startColor, endColor, smooth, progress);
		return new MultiColor(col);
	}

	private static Color getProgressColorIternal(Color startColor, Color endColor, boolean smooth, float progress) {
		if (!smooth) {
			return progress >= 0.95 ? endColor : startColor;
		}

		final int rDiff = endColor.getRed() - startColor.getRed();
		final int gDiff = endColor.getGreen() - startColor.getGreen();
		final int bDiff = endColor.getBlue() - startColor.getBlue();
		final int aDiff = endColor.getAlpha() - startColor.getAlpha();

		return new Color(
				ColorUtils.clamp(startColor.getRed() + (int) (rDiff * progress)),
				ColorUtils.clamp(startColor.getGreen() + (int) (gDiff * progress)),
				ColorUtils.clamp(startColor.getBlue() + (int) (bDiff * progress)),
				ColorUtils.clamp(startColor.getAlpha() + (int) (aDiff * progress))
		);
	}

	public static Box getRenderBox(BlockPos pos, double progress) {
		if (mc.world == null) {
			return PositionMath.boxFromCenter(progress, pos.toCenterPos());
		}

		VoxelShape shape = BlockUtils.getState(pos).getOutlineShape(mc.world, pos);

		if (shape.isEmpty()) {
			return PositionMath.boxFromCenter(progress, pos.toCenterPos());
		}

		Box box = shape.getBoundingBox();
		double p = 0.5 - progress;

		double xShrink = box.getLengthX() * p;
		double yShrink = box.getLengthY() * p;
		double zShrink = box.getLengthZ() * p;

		return box.offset(pos).expand(-xShrink, -yShrink, -zShrink);
	}
}
