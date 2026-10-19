package com.perfect.client.core.render.particle;

import com.perfect.client.core.render.renderer.Renderer3D;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.PositionMath;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import static com.perfect.client.PerfectClient.mc;


public abstract class AbstractParticle {

	public static final float BASE_SIZE = 0.2f;

	protected Vec3d velocity;

	protected final long spawnTime;

	protected Vec3d prevPosition;
	protected Vec3d position;

	protected final MultiColor particleColor;

	protected float scale;

	private boolean skipRender;

	public AbstractParticle(
			Vec3d position,
			Vec3d velocity,
			float scale,
			MultiColor color
	) {
		this.position = position;
		this.velocity = velocity;

		this.scale = scale;
		this.particleColor = color;

		this.spawnTime = System.currentTimeMillis();
	}

	public boolean update() {
		updateMotion();

		return updatePassed();
	}

	protected abstract boolean updatePassed();

	public void updateMotion() {
		prevPosition = position;

		updateParticleMotion();

		skipRender = !PositionMath.isOutOfRange(mc.gameRenderer.getCamera().getPos(), position, 1.5);
	}

	protected abstract void updateParticleMotion();

	public void render(MatrixStack matrices, VertexBuilder buffer, float tickDelta) {
		if (skipRender) {
			return;
		}

		Vec3d pos = MathUtils.interpolateVec3d(prevPosition, position, tickDelta);

		Vec3d center = Renderer3D.getRenderPosition(pos);

		matrices.push();
		matrices.translate(center);

		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-mc.gameRenderer.getCamera().getYaw()));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(mc.gameRenderer.getCamera().getPitch()));

		render(matrices, buffer);

		matrices.pop();
	}

	protected abstract void render(MatrixStack matrices, VertexBuilder buffer);

	protected long getElapsed() {
		return System.currentTimeMillis() - spawnTime;
	}
}