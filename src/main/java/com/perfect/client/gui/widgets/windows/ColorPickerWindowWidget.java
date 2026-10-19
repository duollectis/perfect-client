package com.perfect.client.gui.widgets.windows;

import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;

public class ColorPickerWindowWidget extends WindowWidget {

	public ColorPickerWindowWidget(Widget widget) {
		super(widget);
		setup();
	}

	private void setup() {
		setDisablePanel(true);
		setIndent(3.0f);
		setRounding(3.75f);
		setBackgroundAlpha(1.0f);
	}
}
