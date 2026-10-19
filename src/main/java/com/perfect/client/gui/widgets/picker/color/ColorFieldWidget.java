package com.perfect.client.gui.widgets.picker.color;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.utils.Coloring;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.picker.utils.PointRenderer;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import java.awt.*;

public class ColorFieldWidget extends Widget {

	private static final float POINT_OFFSET = 0.6f;

	@Getter
	@Setter
	private float saturation;
	private float brightness;

	private final FloatAnimation saturationAnimation = new FloatAnimation(0.2)
			.defined(false)
			.target(() -> saturation);

	private final FloatAnimation brightnessAnimation = new FloatAnimation(0.2)
			.defined(false)
			.target(() -> brightness);

	private final PointRenderer pointRenderer = new PointRenderer();

	@Getter
	@Setter
	private boolean changed;

	private final Dragging dragging = new Dragging();

	private float pointFieldX;
	private float pointFieldY;
	private float pointFieldWidth;
	private float pointFieldHeight;

	@Override
	public void onClose() {
		dragging.unPress();
		UiUtils.releaseWidget(this);
	}

	@Override
	public void onPreTick() {
		if (dragging.checkNeedUnPress()) {
			UiUtils.releaseWidget(this);
		}
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setRounding(1.5f);

		float offset = POINT_OFFSET + pointRenderer.getSize() / 2.0f;

		pointFieldWidth = width - offset * 2.0f;
		pointFieldHeight = height - offset * 2.0f;
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		float offset = POINT_OFFSET + pointRenderer.getSize() / 2.0f;

		pointFieldX = drawX + offset;
		pointFieldY = drawY + offset;

		pointRenderer.setCenterX(pointFieldX + pointFieldWidth * saturationAnimation.getCurrent());
		pointRenderer.setCenterY(pointFieldY + pointFieldHeight * brightnessAnimation.getCurrent());
	}

	@Override
	public void onPreDraw() {
		saturationAnimation.updateAndGet();
		brightnessAnimation.updateAndGet();

		if (dragging.isDragging()) {
			float dragX = mouseX - pointFieldX;
			float dragY = mouseY - pointFieldY;

			float s = Math.clamp(dragX / pointFieldWidth, 0.0f, 1.0f);
			float b = Math.clamp(dragY / pointFieldHeight, 0.0f, 1.0f);

			if (b != brightness || s != saturation) {
				changed = true;
			}

			brightness = b;
			saturation = s;
		}
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (isHovered() && event.isPress()) {
			dragging.press(event.getValue());
			UiUtils.setDraggingWidget(this);
		}
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.round()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(0.7f)
		          .softness(0.1f)
		          .color(new Coloring(Color.WHITE, color, Color.BLACK, Color.BLACK))
		          .outlineColor(new Color(28, 30, 47))
		          .draw(matrices);

		pointRenderer.draw(matrices);
	}

	public float getBrightness() {
		return 1.0f - brightness;
	}

	public void setBrightness(float brightness) {
		this.brightness = 1.0f - brightness;
	}

	public void setPointColor(Color color) {
		pointRenderer.setColor(color);
	}
}
