package com.perfect.client.gui.widgets;

import com.perfect.client.gui.WidgetContainer;
import com.perfect.client.gui.utils.WindowSerialize;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.common.button.ToggleButtonWidget;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class WindowContainer {

	private final Widget holdableWidget;
	private final Supplier<WidgetContainer> container;

	private WindowWidget lastWindow;

	private final Consumer<WindowWidget> setupWindowConsumer;
	private final Supplier<WindowSerialize> serializeSupplier;
	private final Supplier<ToggleButtonWidget> linkedButtonSupplier;

	public WindowContainer(
			Supplier<WidgetContainer> container,
			Widget widget,
			Consumer<WindowWidget> setupWindowConsumer,
			Supplier<ToggleButtonWidget> linkedButtonSupplier,
			Supplier<WindowSerialize> serializeSupplier
	) {
		this.container = container;
		this.holdableWidget = widget;
		this.setupWindowConsumer = setupWindowConsumer;
		this.linkedButtonSupplier = linkedButtonSupplier;
		this.serializeSupplier = serializeSupplier;
	}

	public void onInit() {
		if (serializeSupplier.get().isActive()) {
			open();

			if (lastWindow != null) {
				lastWindow.setNormalizedX(serializeSupplier.get().getX());
				lastWindow.setNormalizedY(serializeSupplier.get().getY());
			}

			linkedButtonSupplier.get().setButtonActive(true);
		}
	}

	public void open() {
		if (lastWindow != null) {
			return;
		}

		WindowWidget windowWidget = container.get().addWindow(new WindowWidget(holdableWidget));
		setupWindowConsumer.accept(windowWidget);

		lastWindow = windowWidget;
	}

	public void close() {
		if (lastWindow != null) {
			lastWindow.close();
			lastWindow = null;
		}
	}

	public void onPostDraw() {
		if (lastWindow != null) {
			serializeSupplier.get().setX(lastWindow.getNormalizedX());
			serializeSupplier.get().setY(lastWindow.getNormalizedY());

			serializeSupplier.get().setActive(true);
		}
		else {
			serializeSupplier.get().setActive(false);
		}
	}
}
