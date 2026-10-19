package com.perfect.client.gui.widgets.overlay;

import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import org.joml.Matrix3x2fStack;

public class OverlayListWidget extends VerticalListWidget {

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		getChildren().forEach(widget -> widget.setX(width / 2.0f - widget.getWidth() / 2.0f));
	}
}
