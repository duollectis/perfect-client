package com.perfect.client.functional.function.visuals.killeffect;

import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.functional.function.helper.container.ModeContainer;
import com.perfect.client.functional.function.visuals.killeffect.data.KillEffectMode;
import com.perfect.client.functional.function.visuals.killeffect.modes.LightningMode;
import com.perfect.client.functional.function.visuals.killeffect.modes.ParticlesMode;
import lombok.Getter;

public class KillEffect extends Function {

	private final KillEffectConfig config = new KillEffectConfig(this);

	@Getter
	private final ModeContainer<EffectType, KillEffectMode> modeHandler = createModeContainer(
			config::getKillEffectType
	);

	public KillEffect() {
		super("kill-effect", "Kill Effect", Category.VISUALS);

		modeHandler.add(EffectType.LIGHTNING, new LightningMode(this, config));
		modeHandler.add(EffectType.PARTICLES, new ParticlesMode(this, config));
	}

	public enum EffectType {
		PARTICLES,
		LIGHTNING
	}
}