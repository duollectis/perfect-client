package com.perfect.client.gui.widgets.bind;

import com.perfect.client.core.functional.bind.AbstractBindListener;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import org.joml.Matrix3x2fStack;

public class BindEditWidget extends VerticalListWidget {

	public BindEditWidget(AbstractBindListener bindListener) {
		addChild(new BindButtonWidget(bindListener));
		addChild(new BindModeLineWidget(bindListener));

		setIndent(2.25f);
		expandWidth();
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(48.25f);
		super.onSizeUpdate(matrices);
	}

	public static WindowWidget createWindow(AbstractBindListener bindListener, float mouseX, float mouseY) {
		WindowWidget window = new WindowWidget(new BindEditWidget(bindListener));

		window.setAppearX(() -> mouseX);
		window.setAppearY(() -> mouseY);

		window.setBackgroundAlpha(1.0f);
		window.setIndent(2.25f);
		window.setRounding(3.0f);
		window.setOutlineThickness(0.6f);

		window.setDisablePanel(true);

		return window;
	}
}
