package com.perfect.client.gui.widgets.settings.elements.toggle;

import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.utils.Rounding;
import com.perfect.client.gui.widgets.common.Widget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.function.Supplier;

public class ToggleWidget extends Widget {

	public static final float WIDTH = 7.8f;
	public static final float HEIGHT = 4.4f;

	private static final float POINT_DEPTH = 0.75f;
	private static final float POINT_SIZE = HEIGHT - POINT_DEPTH * 2.0f;
	private static final Rounding POINT_ROUNDING = Rounding.full(POINT_SIZE / 2.0f);

	private final Animation toggleAnimation = new Animation(Easing.OUT_QUAD, 130).finish();

	private float toggleAnim;

	@Setter
	private Supplier<Boolean> toggledSupplier;

	public ToggleWidget(Supplier<Boolean> toggledSupplier) {
		this.toggledSupplier = toggledSupplier;
	}

	public void finishAnimation(boolean direction) {
		toggleAnimation.finish(direction);
	}

	@Override
	public void onOpen() {
		toggleAnimation.finish();
	}

	@Override
	public void onAnimationTick() {
		toggleAnimation.changeDirection(toggledSupplier.get());
		toggleAnim = toggleAnimation.get();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setRounding(height / 2.0f);
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Color boxColor = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.SETTING),
				Managers.COLOR.get(Managers.COLOR.MAIN),
				toggleAnim
		);

		Color pointColor = MathUtils.interpolateColor(
				Managers.COLOR.get(Managers.COLOR.MAIN),
				Managers.COLOR.get(Managers.COLOR.SETTING),
				toggleAnim
		);

		// Рендер бокса
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .color(boxColor)
		          .rounding(rounding)
		          .draw(matrices);

		// Рендер точки
		float pointY = drawY + POINT_DEPTH;

		float pointX = MathUtils.interpolateFloat(
				drawX + POINT_DEPTH,
				drawX + width - POINT_SIZE - POINT_DEPTH,
				toggleAnim
		);
		Renderer2D.round()
		          .pos(pointX, pointY, POINT_SIZE, POINT_SIZE)
		          .rounding(POINT_ROUNDING)
		          .color(pointColor)
		          .draw(matrices);
	}
}
