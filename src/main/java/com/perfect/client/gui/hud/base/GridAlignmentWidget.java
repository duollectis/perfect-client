package com.perfect.client.gui.hud.base;

import com.perfect.client.core.render.animation.value.FloatAnimation;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.utils.MatrixUtils;
import com.perfect.client.gui.utils.Point;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.hudSettings;

public class GridAlignmentWidget extends Widget {

	private final FloatAnimation fadeAnimation = new FloatAnimation(0.125).defined(false).target(0.0f);
	private List<GuideLine> cachedLines = new ArrayList<>();
	private final List<Float> guidesX = new ArrayList<>();
	private final List<Float> guidesY = new ArrayList<>();

	private float draggableX;
	private float draggableY;
	private float draggableX2;
	private float draggableY2;

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		guidesX.clear();
		guidesY.clear();

		Widget dragging = UiUtils.getDraggingWidget();
		if (!(dragging instanceof HudWidget draggable)) {
			fadeAnimation.target(0.0f);
			return;
		}

		float screenW = DrawConstants.getScreenWidth();
		float screenH = DrawConstants.getScreenHeight();

		guidesX.add(0f);
		guidesX.add(screenW);

		guidesY.add(0f);
		guidesY.add(screenH);

		for (Widget widget : parent.getChildren()) {
			if (!(widget instanceof HudWidget hudWidget) || !hudWidget.shouldDraw()) {
				continue;
			}

			boolean scaling = false;
			if (hudWidget.getContainer().get().isCustomScale()) {
				scaling = hudWidget.pushMatricesWithScale(matrices);
			}

			Point min = MatrixUtils.scale(matrices, hudWidget.getDrawX(), hudWidget.getDrawY());
			Point max = MatrixUtils.scale(matrices, hudWidget.getDrawX() + hudWidget.getWidth(), hudWidget.getDrawY() + hudWidget.getHeight());

			if (scaling) {
				matrices.popMatrix();
			}

			if (widget == dragging) {
				draggableX = min.getX();
				draggableY = min.getY();
				draggableX2 = max.getX();
				draggableY2 = max.getY();
			} else {
				guidesX.add(min.getX());
				guidesX.add(max.getX());
				guidesY.add(min.getY());
				guidesY.add(max.getY());
			}
		}

		SnapResult snapX = findBestSnap(draggableX, draggableX2, guidesX);
		SnapResult snapY = findBestSnap(draggableY, draggableY2, guidesY);

		List<GuideLine> activeLinesThisFrame = new ArrayList<>();

		float finalX = draggableX;
		float finalY = draggableY;

		if (snapX != null) {
			finalX = snapX.newPos();
		}
		if (snapY != null) {
			finalY = snapY.newPos();
		}

		if (snapX != null || snapY != null) {
			Point currentReverted = MatrixUtils.revertScale(matrices, draggableX, draggableY);
			Point snappedReverted = MatrixUtils.revertScale(matrices, finalX, finalY);

			if (snapX != null) {
				float deltaX = snappedReverted.getX() - currentReverted.getX();

				draggable.setX(draggable.getX() + deltaX);
				draggable.setNormalizedX(draggable.getX() / screenW);
				activeLinesThisFrame.add(new GuideLine(snapX.guideLine(), 0, snapX.guideLine(), screenH));

				draggable.setDragX(draggable.getMouseX() - draggable.getX());
			}

			if (snapY != null) {
				float deltaY = snappedReverted.getY() - currentReverted.getY();

				draggable.setY(draggable.getY() + deltaY);
				draggable.setNormalizedY(draggable.getY() / screenH);
				activeLinesThisFrame.add(new GuideLine(0, snapY.guideLine(), screenW, snapY.guideLine()));

				draggable.setDragY(draggable.getMouseY() - draggable.getY());
			}
		}

		if (!activeLinesThisFrame.isEmpty()) {
			fadeAnimation.target(1.0f);
			cachedLines = activeLinesThisFrame;
		} else {
			fadeAnimation.target(0.0f);
		}
	}

	private SnapResult findBestSnap(float pos, float pos2, List<Float> guides) {
		float bestDiff = hudSettings.getSnapDistance().getFloat() * hudSettings.getScale().getFloat();
		SnapResult bestSnap = null;

		for (float guide : guides) {
			float leftUpAbs = Math.abs(pos - guide);
			if (leftUpAbs < bestDiff) {
				bestSnap = new SnapResult(guide, guide);
				bestDiff = leftUpAbs;
			}

			float rightDownAbs = Math.abs(pos2 - guide);
			if (rightDownAbs < bestDiff) {
				bestSnap = new SnapResult(guide - (pos2 - pos), guide);
				bestDiff = rightDownAbs;
			}
		}

		return bestSnap;
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		matrices.pushMatrix();
		matrices.identity();

		float alpha = fadeAnimation.updateAndGet();
		if (alpha <= 0.02f) {
			matrices.popMatrix();
			return;
		}

		Color color = new Color(0, 255, 255, (int)(255 * alpha));

		for (GuideLine line : cachedLines) {
			Renderer2D.drawLine(matrices, line.x1, line.y1, line.x2, line.y2, color);
		}

		matrices.popMatrix();
	}

	public record GuideLine(
			float x1,
			float y1,
			float x2,
			float y2
	) {}

	private record SnapResult(
			float newPos,
			float guideLine
	) {}
}