package com.perfect.client.functional.function.visuals.prediction;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;


public class Prediction extends Function {

	private final PredictionConfig config = new PredictionConfig(this);

	public Prediction() {
		super("prediction", "Prediction", Category.VISUALS);
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null || mc.player == null || mc.gameRenderer.getCamera() == null)
			return;

		if (config.getTrajectory().get()) {
			for (Entity entity : mc.world.getEntities()) {
				if (shouldRenderTrajectory(entity)) {
					calcTrajectory(event.getMatrices(), entity);
				}
			}
		}
	}

	private boolean shouldRenderTrajectory(Entity entity) {
		if (!EntityUtils.isMoving(entity)) {
			return false;
		}

		return (config.getPearls().get() && entity instanceof EnderPearlEntity)
				|| (config.getArrows().get() && entity instanceof ArrowEntity)
				|| (config.getTridents().get() && entity instanceof TridentEntity)
				|| (config.getXp().get() && entity instanceof ExperienceBottleEntity)
				|| (config.getPotions().get() && entity instanceof PotionEntity);
	}

	private void calcTrajectory(MatrixStack matrices, Entity e) {
		if (mc.world == null || mc.player == null || e == null)
			return;

		double motionX = e.getVelocity().x;
		double motionY = e.getVelocity().y;
		double motionZ = e.getVelocity().z;

		double x = e.getX();
		double y = e.getY();
		double z = e.getZ();
		Vec3d lastPos;

		for (int i = 0; i < 300; i++) {
			lastPos = new Vec3d(x, y, z);
			x += motionX;
			y += motionY;

			z += motionZ;
			if (mc.world.getBlockState(new BlockPos((int) x, (int) y, (int) z)).getBlock() == Blocks.WATER) {
				motionX *= 0.8;
				motionY *= 0.8;
				motionZ *= 0.8;
			}
			else {
				motionX *= 0.99;
				motionY *= 0.99;
				motionZ *= 0.99;
			}
			motionY -= e.getFinalGravity();
			Vec3d pos = new Vec3d(x, y, z);

			boolean doBreak = false;

			BlockHitResult raycast = mc.world.raycast(new RaycastContext(
					lastPos,
					pos,
					RaycastContext.ShapeType.OUTLINE,
					RaycastContext.FluidHandling.NONE,
					mc.player
			));

			if (raycast != null && raycast.getType() != HitResult.Type.MISS) {
				pos = raycast.getPos();
				doBreak = true;
			}

			if (y <= -65) {
				break;
			}
			if (e.getVelocity().x == 0 && e.getVelocity().y == 0 && e.getVelocity().z == 0) {
				if (doBreak) {
					break;
				}
				continue;
			}

			if (i == 0) {
				lastPos = RenderUtils.getEntityPos(e);
			}

			Renderer3D.drawLine(matrices, lastPos, pos, config.getTrajectoryColor().get().getOffsetColor(i / 75f));

			if (doBreak) {
				break;
			}
		}
	}
}
