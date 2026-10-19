package com.perfect.client.gui.widgets.picker.color;

import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.Orientation;
import com.perfect.client.core.utils.math.MathUtils;
import com.perfect.client.gui.utils.Coloring;
import com.perfect.client.gui.widgets.picker.AbstractPickerSliderWidget;
import com.perfect.client.gui.widgets.picker.utils.PointRenderer;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class PickerSliderWidget extends AbstractPickerSliderWidget {

	private static final float SLIDER_HEIGHT = 2.0f;

	private final PointRenderer pointRenderer = new PointRenderer();

	private final boolean hue;

	private float pointFieldX;
	private float pointFieldWidth;

	public PickerSliderWidget(boolean hue) {
		this.hue = hue;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		pointFieldX = drawX + pointRenderer.getSize() / 2.0f;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		super.onSizeUpdate(matrices);
		pointFieldWidth = width - pointRenderer.getSize();
	}

	@Override
	protected float getDraggableX() {
		return pointFieldX;
	}

	@Override
	protected float getDraggableWidth() {
		return pointFieldWidth;
	}

	@Override
	protected void draw(Matrix3x2fStack matrices) {
		if (hue) {
			Renderer2D.HUE_SHADER.draw(
					matrices,
					drawX,
					centerY - SLIDER_HEIGHT / 2.0f,
					width,
					SLIDER_HEIGHT,
					SLIDER_HEIGHT / 2.0f,
					0.2f, 1.0f,
					0.0f, Orientation.HORIZONTAL
			);
		}
		else {
			Renderer2D.round()
			          .pos(drawX, centerY - SLIDER_HEIGHT / 2.0f, width, SLIDER_HEIGHT)
			          .rounding(SLIDER_HEIGHT / 2.0f)
			          .color(Coloring.horizontalGradient(Color.WHITE, color))
			          .draw(matrices);
		}

		float pointValueWidth = pointFieldWidth * valueAnimation.getCurrent();

		pointRenderer.setCenterX(pointFieldX + pointValueWidth);
		pointRenderer.setCenterY(centerY);
		pointRenderer.setColor(getPointColor());
		pointRenderer.draw(matrices);
	}

	private Color getPointColor() {
		if (hue) {
			return Color.getHSBColor(valueAnimation.getCurrent(), 1.0f, 1.0f);
		}
		else {
			return MathUtils.interpolateColor(Color.WHITE, color, valueAnimation.getCurrent());
		}
	}
}
