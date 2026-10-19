package com.perfect.client.manager.game.screen;

import com.perfect.client.gui.widgets.common.Widget;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OverlayElement<T extends Widget> {

	private final Supplier<T> widgetSupplier;
	private final Supplier<Boolean> openCondition;
	private final Consumer<T> openConsumer;

	private T widget;

	public OverlayElement(
			Supplier<T> widgetSupplier,
			Supplier<Boolean> openCondition,
			Consumer<T> openConsumer
	) {
		this.widgetSupplier = widgetSupplier;
		this.openCondition = openCondition;
		this.openConsumer = openConsumer;
	}

	public void tick() {
		if (openCondition.get()) {
			open();
		}
		else {
			close();
		}

		if (widget != null && widget.isDestroyed() && widget.isAnimationFinished()) {
			widget = null;
		}
	}

	public void open() {
		if (widget == null || widget.isDestroyed()) {
			widget = widgetSupplier.get();
			openConsumer.accept(widget);
		}
	}

	public final void close() {
		if (widget != null) {
			close(widget);
		}
	}

	protected void close(T widget) {
		widget.setDestroyed(true);
	}
}
