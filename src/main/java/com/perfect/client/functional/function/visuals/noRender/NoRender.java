package com.perfect.client.functional.function.visuals.noRender;

import com.perfect.client.core.event.events.render.ParticleEvent;
import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;

public class NoRender extends Function {

	private final NoRenderConfig config = new NoRenderConfig(this);

	public NoRender() {
		super("no-render", "No Render", Category.VISUALS);
	}

	@Override
	public void onParticle(ParticleEvent event) {
		if (contains(Elements.PARTICLES)) {
			event.cancel();
		}
		else if (contains(Elements.EXPLOSIONS) && isExplosion(event.getParticle().getType())) {
			event.cancel();
		}
	}

	private boolean isExplosion(ParticleType<?> type) {
		return type == ParticleTypes.EXPLOSION || type == ParticleTypes.EXPLOSION_EMITTER;
	}

	@Override
	public void onRender2D(Render2DEvent event) {
		if (contains(Elements.LOADING_SCREEN) && mc.currentScreen instanceof DownloadingTerrainScreen) {
			mc.setScreen(null);
		}
	}

	
	public boolean contains(Elements element) {
		return isEnabled() && config.getElements().get(element);
	}

	
	public boolean contains(Entity entity) {
		return isEnabled() && config.getEntities().contains(entity.getType());
	}
}
