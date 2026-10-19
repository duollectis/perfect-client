package com.perfect.client.core.render;

import com.perfect.client.PerfectClient;
import com.perfect.client.core.event.events.render.Render3DEvent;
import com.perfect.client.core.event.system.IEventListener;
import com.perfect.client.core.functional.setting.bundles.costemics.AnimatedCosmetics;
import com.perfect.client.core.functional.setting.bundles.costemics.Cosmetics;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.IEasing;
import com.perfect.client.core.render.box.BoxRenderer;
import com.perfect.client.core.utils.color.multiColor.MultiColor;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.core.utils.math.TimerUtils;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class PlaceRenderer implements IEventListener {

	private final List<BlockRender> blocks = new CopyOnWriteArrayList<>();

	private final Supplier<MultiColor> lineColorSupplier;
	private final Supplier<MultiColor> sideColorSupplier;
	private final Supplier<AnimatedCosmetics> cosmeticsSupplier;

	@Setter
	private Supplier<Boolean> pauseSupplier = () -> false;

	public PlaceRenderer(
			Supplier<MultiColor> lineColorSupplier,
			Supplier<MultiColor> sideColorSupplier,
			Supplier<AnimatedCosmetics> cosmeticsSupplier
	) {
		this.lineColorSupplier = lineColorSupplier;
		this.sideColorSupplier = sideColorSupplier;
		this.cosmeticsSupplier = cosmeticsSupplier;

		PerfectClient.EVENT_MANAGER.subscribe(this);
	}

	public PlaceRenderer pauseSupplier(Supplier<Boolean> pauseSupplier) {
		this.pauseSupplier = pauseSupplier;
		return this;
	}

	@Override
	public void onPreTick() {
		blocks.removeIf(render -> render.animationTimer.passedMillis(render.renderTime));
	}

	@Override
	public void onRender3D(Render3DEvent event) {
		if (blocks.isEmpty()) {
			return;
		}
		render(event, lineColorSupplier.get(), sideColorSupplier.get(), cosmeticsSupplier.get());
	}

	private void render(Render3DEvent event, MultiColor lineColor, MultiColor sideColor, AnimatedCosmetics cosmetics) {
		blocks.forEach(blockRender -> {
			if (blockRender != null) {
				if (pauseSupplier.get()) {
					blockRender.reset(cosmetics);
				}
				blockRender.render(event, lineColor, sideColor, cosmetics);
			}
		});
	}

	public void clear() {
		blocks.clear();
	}

	public void add(BlockPos pos) {
		add(new Box(pos));
	}

	public void add(Box box) {
		add(box, cosmeticsSupplier.get());
	}

	public void add(Box box, AnimatedCosmetics cosmetics) {
		for (BlockRender block : blocks) {
			if (block.box.equals(box)) {
				block.reset(cosmetics);
				return;
			}
		}
		blocks.add(new BlockRender(box, cosmetics));
	}

	public static class BlockRender {

		public final Box box;

		public final TimerUtils animationTimer = new TimerUtils();

		public IEasing easing;
		public int renderTime;

		public Animation opacityAnimation;
		public Animation scaleAnimation;

		public BlockRender(Box box, AnimatedCosmetics cosmetics) {
			this.box = box;
			renderTime = cosmetics.getAnimationTimeMillis();
			easing = cosmetics.getEasing();

			if (cosmetics.isOpacityAnimation()) {
				opacityAnimation = new Animation(animationTimer, easing, 1.0f, renderTime, false);
			}
			if (cosmetics.isScaleAnimation()) {
				scaleAnimation = new Animation(animationTimer, easing, 1.0f, renderTime, true);
			}
		}

		public void reset(AnimatedCosmetics cosmetics) {
			renderTime = cosmetics.getAnimationTimeMillis();
			easing = cosmetics.getEasing();
			animationTimer.reset();

			if (opacityAnimation != null) {
				opacityAnimation.setTime(renderTime);
				opacityAnimation.setEasing(easing);
			}
			if (scaleAnimation != null) {
				scaleAnimation.setTime(renderTime);
				scaleAnimation.setEasing(easing);
			}
		}

		public void render(Render3DEvent event, MultiColor lineColor, MultiColor sideColor, Cosmetics cosmetics) {

			float opacity = 1.0f;

			if (opacityAnimation != null) {
				opacity = MathUtils.interpolateFloat(0.0f, 1.0f, opacityAnimation.get());
			}

			Box renderBox = box;

			if (scaleAnimation != null) {
				double p = scaleAnimation.get() * 0.5f;

				double xShrink = box.getLengthX() * p;
				double yShrink = box.getLengthY() * p;
				double zShrink = box.getLengthZ() * p;

				renderBox = box.expand(-xShrink, -yShrink, -zShrink);
			}

			BoxRenderer.drawBox(
					event.getMatrices(),
					renderBox,
					lineColor.opacity(opacity),
					sideColor.opacity(opacity),
					cosmetics
			);
		}
	}
}
