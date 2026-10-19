package com.perfect.client.functional.function.visuals.arrows;

import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.functional.function.Category;
import com.perfect.client.core.functional.function.Function;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Render;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.render.renderer.blending.DstFactor;
import com.perfect.client.core.render.renderer.blending.SrcFactor;
import com.perfect.client.core.render.texture.Textures;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.core.utils.color.ColorUtils;
import com.perfect.client.core.utils.game.entity.EntityUtils;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.functional.function.visuals.arrows.data.Arrow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Arrows extends Function {

	private static final int MIN_ANIMATION_TIME = -1;
	private static final int MAX_ANIMATION_TIME = 500;

	private final ArrowsConfig config = new ArrowsConfig(this);

	private final List<Arrow> arrowList = new ArrayList<>();

	private final FloatAnimation yawAnimation = new FloatAnimation(0.2);

	public Arrows() {
		super("arrows", "Arrows", Category.VISUALS);
	}

	@Override
	public void onDisable() {
		reset();
	}

	@Override
	public void onRespawn() {
		reset();
	}

	@Override
	public void onGameLeft() {
		reset();
	}

	public void reset() {
		arrowList.clear();
	}

	@Override
	public void onPreTick() {
		if (mc.player == null || mc.world == null) {
			return;
		}

		for (Entity entity : mc.world.getPlayers()) {
			if (!(entity instanceof PlayerEntity player)) {
				continue;
			}
			if (mc.player.equals(player)) {
				continue;
			}
			if (RenderUtils.shouldSkipEntityRender(player)) {
				continue;
			}

			if (!isArrowsContains(player)) {
				arrowList.add(new Arrow(player, getAnimationTime()));
			}
		}

		for (Arrow arrow : arrowList) {
			arrow.setVisible(EntityUtils.isExist(arrow.getPlayer()));
		}

		arrowList.removeIf(Arrow::isFinished);
	}

	private int getAnimationTime() {
		return MathUtils.interpolateInt(
				MIN_ANIMATION_TIME,
				MAX_ANIMATION_TIME,
				config.getAnimationTime().fromPercent()
		);
	}

	private boolean isArrowsContains(PlayerEntity player) {
		for (Arrow arrow : arrowList) {
			if (arrow.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}

	
	@Override
	public void onRender2D(Render2DEvent event) {
		if (mc.world == null || mc.gameRenderer.getCamera() == null) {
			return;
		}

		Matrix3x2fStack matrices = event.getContext().getMatrices();

		float centerX = mc.getWindow().getScaledWidth() / 2f;
		float centerY = mc.getWindow().getScaledHeight() / 2f;

		float animatedYaw = yawAnimation.updateAndGet(mc.gameRenderer.getCamera().getYaw());

		for (Arrow arrow : arrowList) {
			Entity entity = arrow.getPlayer();
			float animation = arrow.getAnimation();

			float yaw = getRotations(entity) - animatedYaw;

			float animatedOffset = config.getAnimationOffset().getFloat() * (1f - animation);

			matrices.pushMatrix();

			matrices.translate(centerX, centerY);
			matrices.rotate((float) Math.toRadians(yaw));
			matrices.translate(-centerX, -centerY);

			Renderer2D.drawCenteredTexture(
					matrices,
					Textures.ARROW.getGlId(),
					centerX,
					centerY - (config.getRadius().getFloat() + animatedOffset),
					config.getScale().getFloat(),
					getColor(entity, animation),
					() -> Render.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE)
			);

			matrices.popMatrix();
		}
	}

	private Color getColor(Entity entity, float animation) {
		Color entityColor = RenderUtils.getEntityColor(entity, config.getColor().get().getColor());
		return ColorUtils.multiplyAlpha(entityColor, animation);
	}

	public float getRotations(Entity entity) {
		double x = interp(entity.getPos().x, entity.lastX) - mc.gameRenderer.getCamera().getPos().x;
		double z = interp(entity.getPos().z, entity.lastZ) - mc.gameRenderer.getCamera().getPos().z;

		return (float) -(Math.atan2(x, z) * (180 / Math.PI));
	}

	public double interp(double d, double d2) {
		return d2 + (d - d2) * (double) RenderUtils.getTickDelta();
	}
}
