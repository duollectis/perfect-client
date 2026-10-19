package com.perfect.client.core.render.particle;

import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.inject.mixinInterface.IVec3d;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import org.joml.Matrix4f;

import java.awt.*;

import static com.perfect.client.PerfectClient.mc;
import static com.perfect.client.core.utils.math.MathUtils.offset;


public class StandardParticle extends AbstractParticle {

	private static final float ROTATION_SPEED = 0.15f;
	private static final float GRAVITY_FORCE = 0.00124f;

	private Box boundingBox;

	private float rotation;
	private int alpha;

	private final float massFactor;

	private boolean fadeIn = true;
	private boolean reachedUpwardVelocity;

	public StandardParticle(
			Vec3d startPos,
			double factor,
			float scale,
			MultiColor color
	) {
		super(
				startPos.add(offset(0.25), offset(0.1), offset(0.25)),
				new Vec3d(offset(1.0 / factor), 0.0, offset(1.0 / factor)),
				scale,
				color
		);

		massFactor = (float) factor;
		boundingBox = createBoundingBox(position);
	}

	@Override
	protected boolean updatePassed() {
		if (fadeIn) {
			alpha += 17;
			if (isFadeInEnded()) {
				fadeIn = false;
			}
		}
		else {
			alpha -= 12;
		}

		alpha = MathHelper.clamp(alpha, 0, 255);

		return !fadeIn && alpha == 0;
	}

	@Override
	protected void updateParticleMotion() {
		rotation += ROTATION_SPEED;
		boundingBox = updateBoundingBox();

		updateUpwardVelocity();
		handleWallCollisions();

		((IVec3d) position).perfect$set(position.x + velocity.x, position.y + velocity.y, position.z + velocity.z);
	}

	@Override
	protected void render(MatrixStack matrices, VertexBuilder builder) {
		matrices.scale(-scale, -scale, -scale);
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));

		Matrix4f matrix = matrices.peek().getPositionMatrix();
		Color color = ColorUtils.applyAlpha(particleColor.getColor(), (int) (alpha * particleColor.getAlpha()));

		builder.pos(matrix, -BASE_SIZE, BASE_SIZE, 0).tex(0, 1).color(color);
		builder.pos(matrix, BASE_SIZE, BASE_SIZE, 0).tex(1, 1).color(color);
		builder.pos(matrix, BASE_SIZE, -BASE_SIZE, 0).tex(1, 0).color(color);
		builder.pos(matrix, -BASE_SIZE, -BASE_SIZE, 0).tex(0, 0).color(color);
	}

	private void updateUpwardVelocity() {
		if (!reachedUpwardVelocity && velocity.y < 0.01f) {
			double y = velocity.y + GRAVITY_FORCE / massFactor;

			((IVec3d) velocity).perfect$setY(y);

			if (y >= 0.01f) {
				reachedUpwardVelocity = true;
			}
		}
	}

	private boolean isFadeInEnded() {
		return alpha >= 255;
	}

	private void handleWallCollisions() {
		BlockPos pos = BlockPos.ofFloored(position.x, position.y - BASE_SIZE, position.z);

		if (!collidesAt(pos)) {
			return;
		}

		double dx = position.x - pos.getX();
		double dz = position.z - pos.getZ();

		Direction best = null;
		double bestDist = Double.MAX_VALUE;

		for (Direction d : Direction.values()) {
			if (d.getAxis().isVertical()) {
				continue;
			}

			double offset = d.getAxis().choose(dx, 0.0, dz);
			double dist = d.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - offset : offset;

			if (dist < bestDist && !collidesAt(pos.offset(d))) {
				best = d;
				bestDist = dist;
			}
		}

		if (best == null) {
			return;
		}

		if (best.getAxis() == Direction.Axis.X) {
			velocity = velocity.multiply(new Vec3d(-1.0, 1.0, 1.0));
		}
		else {
			velocity = velocity.multiply(new Vec3d(1.0, 1.0, -1.0));
		}
	}

	private boolean collidesAt(BlockPos pos) {
		if (mc.world == null) {
			return false;
		}

		Box block = new Box(
				pos.getX(),
				boundingBox.minY + BASE_SIZE,
				pos.getZ(),
				pos.getX() + 1.0,
				boundingBox.maxY + BASE_SIZE,
				pos.getZ() + 1.0
		).contract(1e-7);

		return !mc.world.isBlockSpaceEmpty(null, block);
	}

	private Box createBoundingBox(Vec3d p) {
		return new Box(
				p.x - BASE_SIZE, p.y - BASE_SIZE, p.z - BASE_SIZE,
				p.x + BASE_SIZE, p.y + BASE_SIZE, p.z + BASE_SIZE
		);
	}

	private Box updateBoundingBox() {
		float w = BASE_SIZE * 1.5f;
		return new Box(
				position.x - w, position.y - BASE_SIZE, position.z - w,
				position.x + w, position.y + BASE_SIZE, position.z + w
		);
	}
}