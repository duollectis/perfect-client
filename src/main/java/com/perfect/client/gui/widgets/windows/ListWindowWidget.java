package com.perfect.client.gui.widgets.windows;

import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;

public class ListWindowWidget extends WindowWidget {

	public ListWindowWidget(Widget widget) {
		super(widget);

		setDisablePanel(true);
		setIndent(3.5f);
		setRounding(4.5f);
		setBackgroundAlpha(1.0f);
	}
}
