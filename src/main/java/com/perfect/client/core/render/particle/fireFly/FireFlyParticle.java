package com.perfect.client.core.render.particle.fireFly;

import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.particle.AbstractParticle;
import com.perfect.client.core.render.system.vertex.VertexBuilder;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.*;


public class FireFlyParticle extends AbstractParticle {

	private final IParticlePhysics physics;

	private final long lifeTime;

	private final FloatAnimation alphaAnimation = new FloatAnimation(10.0).target(1.0f);

	public FireFlyParticle(
			Vec3d position,
			float speed,
			float scale,
			MultiColor color,
			long lifetime,
			IParticlePhysics physics
	) {
		super(
				position,
				physics.getStartVelocity(speed),
				scale,
				color
		);
		this.physics = physics;
		this.lifeTime = lifetime;
	}

	@Override
	protected boolean updatePassed() {
		return getElapsed() > lifeTime;
	}

	@Override
	protected void updateParticleMotion() {
		alphaAnimation.updateAndGet();

		velocity = physics.getVelocity(position, velocity);
		position = position.add(velocity);
	}

	@Override
	protected void render(MatrixStack matrices, VertexBuilder builder) {
		matrices.scale(scale, scale, scale);

		float lifeFade = 1.0f - (getElapsed() / 5000.0f);
		float size = BASE_SIZE * lifeFade;

		Color color = ColorUtils.multiplyAlpha(particleColor.getColor(), alphaAnimation.getCurrent() * lifeFade);
		Matrix4f matrix = matrices.peek().getPositionMatrix();

		builder.pos(matrix, -size, size, 0.0f).tex(0.0f, 1.0f).color(color);
		builder.pos(matrix, size, size, 0.0f).tex(1.0f, 1.0f).color(color);
		builder.pos(matrix, size, -size, 0.0f).tex(1.0f, 0.0f).color(color);
		builder.pos(matrix, -size, -size, 0.0f).tex(0.0f, 0.0f).color(color);
	}
}