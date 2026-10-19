package com.perfect.client.functional.function.visuals.firefly;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.particle.fireFly.FireFlyParticle;
import com.perfect.client.core.render.particle.fireFly.IParticlePhysics;
import com.perfect.client.core.render.particle.physics.FallPhysics;
import com.perfect.client.core.render.particle.physics.FlyPhysics;
import com.perfect.client.core.render.particle.physics.InvertedPhysics;
import com.perfect.client.core.utils.math.MathUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class FireFly extends Function {

	private static final int RANGE = 45;

	private final FireFlyConfig config = new FireFlyConfig(this);

	public FireFly() {
		super("fire-fly", "Fire Fly", Category.VISUALS);
	}

	@Override
	public void onDisable() {
		Managers.PARTICLE.clear();
	}

	@Override
	public void onPostTick() {
		if (mc.world == null || mc.player == null || mc.gameRenderer.getCamera() == null) {
			return;
		}

		spawnParticles();
	}

	private void spawnParticles() {
		int count = config.getCount().get();

		for (int i = 0; i < count; i++) {
			Vec3d pos = getPos();

			Managers.PARTICLE.add(new FireFlyParticle(
					pos,
					config.getSpeed().getFloat(),
					(float) config.getParticleScale().fromPercent(),
					config.getColorSetting().get(),
					config.getLifeTime().fromSeconds(),
					config.getParticleType().get().physics
			));
		}
	}

	private Vec3d getPos() {
		assert mc.player != null && mc.world != null;

		Vec3d camera = mc.gameRenderer.getCamera().getPos();

		Vec3d offset = camera.add(
				MathUtils.random(-RANGE, RANGE),
				0,
				MathUtils.random(-RANGE, RANGE)
		);

		BlockPos top = mc.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, BlockPos.ofFloored(offset));

		double x = top.getX() + MathUtils.random(0f, 1f);
		double y = camera.getY() + MathUtils.random(-1, RANGE);
		double z = top.getZ() + MathUtils.random(0f, 1f);

		if (config.getParticleType().is(ParticleType.INVERTED)) {
			y = top.getY();
		}

		return new Vec3d(x, y, z);
	}

	public enum ParticleType {
		FLY(new FlyPhysics()),
		FALL(new FallPhysics()),
		INVERTED(new InvertedPhysics());

		public final IParticlePhysics physics;

		ParticleType(IParticlePhysics physics) {
			this.physics = physics;
		}
	}
}