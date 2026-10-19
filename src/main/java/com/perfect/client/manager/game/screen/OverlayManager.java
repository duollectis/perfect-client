package com.perfect.client.manager.game.screen;

import com.perfect.client.core.event.system.Cancellable;
import com.perfect.client.core.manager.Manager;
import com.perfect.client.gui.WidgetContainer;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import com.perfect.client.gui.widgets.overlay.OverlayWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OverlayManager extends Manager {

	private final List<OverlayElement<?>> overlayElements = new ArrayList<>();

	private final WidgetContainer widgetContainer = new WidgetContainer().customScale();

	private final OverlayWidget overlay = widgetContainer.addChild(new OverlayWidget());

	public OverlayManager() {
		super();

		widgetContainer.open();
	}

	public void addWindow(
			Supplier<WindowWidget> widgetSupplier,
			Supplier<Boolean> openCondition
	) {
		overlayElements.add(new OverlayWindow(
				widgetSupplier,
				openCondition,
				() -> widgetContainer
		));
	}

	public void addUpperOverlay(
			Supplier<Widget> widgetSupplier,
			Supplier<Boolean> openCondition
	) {
		overlayElements.add(new OverlayElement<>(
				widgetSupplier,
				openCondition,
				overlay::addUpperOverlay
		));
	}

	@Override
	public void onPreTick() {
		overlayElements.forEach(OverlayElement::tick);
	}

	@Override
	public void onScreenMouse(Cancellable event) {
		if (widgetContainer.isOverlayHovered()) {
			event.cancel();
		}
	}

	@Override
	public void onScreenKey(Cancellable event) {
		if (UiUtils.isListening()) {
			event.cancel();
		}
	}
}
