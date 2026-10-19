package com.perfect.client.functional.function.visuals.chams;

import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.postprocess.processors.ChamsProcessor;
import com.perfect.client.core.render.renderer.ModelRenderer;
import com.perfect.client.core.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class Chams extends Function {

	private final ChamsConfig config = new ChamsConfig(this);

	public Chams() {
		super("chams", "Chams", Category.VISUALS);
	}

	@Override
	public void onEnable() {
		if (config.getMode().get(Mode.IMAGE)) {
			ChamsProcessor.load();
		}
	}

	@Override
	
	public void onRender3D(Render3DEvent event) {
		if (mc.world == null) {
			return;
		}

		if (!config.isOutlineMode()) {
			return;
		}

		for (Entity entity : mc.world.getEntities()) {
			if (!shouldDraw(entity)) {
				continue;
			}
			if (getIgnoreSelf() && entity == mc.player) {
				continue;
			}
			if (RenderUtils.shouldSkipEntityRender(entity)) {
				continue;
			}

			ModelRenderer.render(event, 1f, entity, getOutlineColor(entity), getFillColor(entity));
		}
	}

	public boolean isShaderMode() {
		return isEnabled() && config.getMode().get(Mode.IMAGE);
	}

	public Color getShaderColor() {
		return config.getShaderColor().get().getColor();
	}

	public boolean shouldDraw(Entity entity) {
		if (entity == null) {
			return false;
		}

		Vec3d camera = mc.gameRenderer.getCamera().getPos();
		if (camera.distanceTo(entity.getPos()) > config.getRenderDistance().get()) {
			return false;
		}

		if (entity instanceof EndCrystalEntity) {
			return getCrystals();
		}

		return entity instanceof PlayerEntity && getPlayers();
	}

	public Color getOutlineColor(Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return config.getCrystalsOutlineColor().get().getColor();
		} else {
			return config.getPlayersOutlineColor().get().getColor();
		}
	}

	public Color getFillColor(Entity entity) {
		if (entity instanceof EndCrystalEntity) {
			return config.getCrystalsFillColor().get().getColor();
		} else {
			return config.getPlayersFillColor().get().getColor();
		}
	}

	public boolean getCrystals() {
		return isEnabled() && config.getCrystals().get();
	}

	public double getCrystalSize() {
		return config.getCrystalsScale().fromPercent();
	}

	public double getCrystalBounce() {
		return config.getCrystalsBounce().fromPercent();
	}

	public double getCrystalBounceSpeed() {
		return config.getCrystalsBounceSpeed().fromPercent();
	}

	public double getCrystalsRotationSpeed() {
		return config.getCrystalsRotationSpeed().fromPercent();
	}

	public boolean getCrystalsChangeColor() {
		return config.getCrystalsColor().isEnabled();
	}

	public Color getCrystalsColor() {
		return config.getCrystalsColor().get().getColor();
	}

	public boolean getPlayers() {
		return isEnabled() && config.getPlayers().get();
	}

	public boolean getIgnoreSelf() {
		return config.getIgnoreSelf().get();
	}

	public boolean getPlayersChangeColor() {
		return config.getPlayersColor().isEnabled();
	}

	public Color getPlayerColor() {
		return config.getPlayersColor().get().getColor();
	}

	public enum Mode {
		NONE,
		OUTLINE,
		IMAGE
	}
}
