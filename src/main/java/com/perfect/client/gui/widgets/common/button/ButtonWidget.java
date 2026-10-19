package com.perfect.client.gui.widgets.common.button;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.utils.Coloring;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class ButtonWidget extends Widget {

	@Setter
	private Runnable action;

	@Setter
	private Runnable rightClickAction;

	@Setter
	private Supplier<Color> outlineColorSupplier = () -> Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE);

	@Setter
	private Supplier<Coloring> backgroundColorSupplier = () -> Coloring.solid(Managers.COLOR.get(Managers.COLOR.BACKGROUND));

	@Setter
	private float outlineSize = 0.6f;

	@Setter
	private float outlineSoftness = 0.2f;

	@Setter
	private Supplier<Float> alphaSupplier = () -> 0.9f;

	@Setter
	private Supplier<Float> outlineAlphaSupplier = () -> 1.0f;

	private final Animation hoveredAnimation = new Animation(Easing.IN_OUT_SINE, 300, false).finish();

	private boolean blurred;

	protected float hoveredAnim;

	public ButtonWidget(Runnable action) {
		this.action = action;

		width = 25.0f;
		height = 10.0f;
	}

	@SuppressWarnings("unchecked")
	public <T extends ButtonWidget> T backgroundColor(Supplier<Color> colorSupplier) {
		this.backgroundColorSupplier = () -> Coloring.solid(colorSupplier.get());
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ButtonWidget> T add(Widget widget) {
		addChild(widget);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends ButtonWidget> T blurred() {
		blurred = true;
		return (T) this;
	}

	@Override
	public void onOpen() {
		hoveredAnimation.finish();
	}

	@Override
	public void onAnimationTick() {
		hoveredAnim = hoveredAnimation.changeDirectionAndGet(isHovered());
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Color outlineColor = outlineColorSupplier.get();
		Coloring plateColor = backgroundColorSupplier.get();
		float alpha = alphaSupplier.get();
		float outlineAlpha = outlineAlphaSupplier.get();

		if (blurred) {
			Renderer2D.blurredRound()
			          .pos(drawX, drawY, width, height)
			          .rounding(rounding)
			          .thickness(outlineSize)
			          .softness(outlineSoftness)
			          .blur(2.0f)
			          .alpha(alpha)
			          .outlineAlpha(outlineAlpha)
			          .outlineColor(outlineColor)
			          .color(plateColor)
			          .draw(matrices);
		}
		else {
			Renderer2D.round()
			          .pos(drawX, drawY, width, height)
			          .rounding(rounding)
			          .thickness(outlineSize)
			          .softness(outlineSoftness)
			          .alpha(alpha)
			          .outlineAlpha(outlineAlpha)
			          .outlineColor(outlineColor)
			          .color(plateColor)
			          .draw(matrices);
		}
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (button.isClick(event, mouseX, mouseY)) {
			if (event.isLeft() || rightClickAction == null) {
				if (action != null) {
					action.run();
				}
			}
			else if (event.isRight() && rightClickAction != null) {
				rightClickAction.run();
			}
		}
	}
}
