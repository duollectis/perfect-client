package com.perfect.client.manager.game.screen;

import com.perfect.client.gui.WidgetContainer;
import com.perfect.client.gui.widgets.common.WindowWidget;

import java.util.function.Supplier;

public class OverlayWindow extends OverlayElement<WindowWidget> {

	public OverlayWindow(
			Supplier<WindowWidget> widgetSupplier,
			Supplier<Boolean> openCondition,
			Supplier<WidgetContainer> container
	) {
		super(widgetSupplier, openCondition, widget -> {
			widget.setCloseable(false);
			container.get().addWindow(widget);
		});
	}

	@Override
	public void close(WindowWidget widget) {
		widget.close();
	}
}
