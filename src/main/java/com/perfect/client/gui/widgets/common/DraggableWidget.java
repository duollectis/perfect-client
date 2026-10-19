package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.render.animation.AnimationUtils;
import com.perfect.client.core.utils.widget.Dragging;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import com.perfect.client.gui.utils.UiUtils;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix3x2fStack;

import static com.perfect.client.PerfectClient.mc;

public class DraggableWidget extends Widget {

	@Getter
	@Setter
	private float normalizedX, normalizedY;

	@Getter
	@Setter
	private float dragX, dragY;

	protected final Dragging dragging = new Dragging();

	@Override
	public void onPreTick() {
		dragging.checkNeedUnPress();
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		if (dragging.isDragging()) {
			float lerpX = AnimationUtils.animateFloat(
					getX(),
					mouseX - dragX,
					0.2f
			);

			float lerpY = AnimationUtils.animateFloat(
					getY(),
					mouseY - dragY,
					0.2f
			);

			normalizedX = lerpX / mc.getWindow().getScaledWidth();
			normalizedY = lerpY / mc.getWindow().getScaledHeight();
		}

		Point pos = normalizePosition(
				matrices,
				normalizedX * mc.getWindow().getScaledWidth(),
				normalizedY * mc.getWindow().getScaledHeight()
		);

		setX(pos.getX());
		setY(pos.getY());

		super.onPositionUpdate(matrices);
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		if (!dragging.isDragging() && isDragHovered() && event.isLeftOrRightPress()) {
			dragging.press(event.getValue());
			dragX = mouseX - getX();
			dragY = mouseY - getY();

			UiUtils.setDraggingWidget(this);
		}
	}

	protected void setPosition(float x, float y) {
		if (mc.getWindow() == null) {
			return;
		}

		normalizedX = x / mc.getWindow().getScaledWidth();
		normalizedY = y / mc.getWindow().getScaledHeight();
	}

	public boolean isDragHovered() {
		return isHovered();
	}

	private Point normalizePosition(Matrix3x2fStack matrices, float x, float y) {
		boolean scaling = container.get().isCustomScale() && pushMatricesWithScale(matrices);

		Point scaled = MatrixUtils.scale(matrices, x, y);

		x = scaled.getX();
		y = scaled.getY();

		if (parent != null) {
			Point parentMinPos = MatrixUtils.scale(matrices, parent.getDrawX(), parent.getDrawY());
			Point parentMaxPos = MatrixUtils.scale(
					matrices,
					parent.getDrawX() + parent.getWidth() - width,
					parent.getDrawY() + parent.getHeight() - height
			);

			x = Math.max(Math.min(x, parentMaxPos.getX()), parentMinPos.getX());
			y = Math.max(Math.min(y, parentMaxPos.getY()), parentMinPos.getY());
		}

		Point reverted = MatrixUtils.revertScale(matrices, x, y);

		x = reverted.getX();
		y = reverted.getY();

		Point revertedScreenZero = MatrixUtils.revertScale(matrices, 0, 0);

		Point revertedScreenSize = MatrixUtils.revertScale(
				matrices,
				DrawConstants.getScreenWidth(),
				DrawConstants.getScreenHeight()
		);

		x = Math.max(revertedScreenZero.getX(), Math.min(x, revertedScreenSize.getX() - width));
		y = Math.max(revertedScreenZero.getY(), Math.min(y, revertedScreenSize.getY() - height));

		if (scaling) {
			matrices.popMatrix();
		}

		return new Point(x, y);
	}
}
