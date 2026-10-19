package com.perfect.client.functional.function.visuals.particles;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.SetModeContainer;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.functional.function.visuals.particles.handlers.ProjectileHandler;
import com.perfect.client.functional.function.visuals.particles.handlers.TotemPopHandler;
import com.perfect.client.functional.function.visuals.particles.handlers.WalkHandler;
import com.perfect.client.functional.function.visuals.particles.utils.ParticlesHandler;
import lombok.Getter;

public class Particles extends Function {

	private final ParticlesConfig config = new ParticlesConfig(this);

	@Getter
	private final SetModeContainer<Condition, ParticlesHandler> setModeContainer = createListModeContainer(config::getElements)
			.add(Condition.TOTEM_POP, new TotemPopHandler(this, config))
			.add(Condition.WALK, new WalkHandler(this, config))
			.add(Condition.PEARL, new ProjectileHandler(this, config))
			.add(Condition.TRIDENT, new ProjectileHandler(this, config))
			.add(Condition.BOW, new ProjectileHandler(this, config));

	public Particles() {
		super("particles", "Particles", Category.VISUALS);
	}

	@Override
	public void onDisable() {
		Managers.PARTICLE.clear();
	}

	public enum Condition {
		TOTEM_POP,
		WALK,
		PEARL,
		TRIDENT,
		BOW
	}
}