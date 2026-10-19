package com.perfect.client.gui;

import com.perfect.client.core.event.events.input.keyboard.CharEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.event.events.input.mouse.MouseScrollEvent;
import com.perfect.client.core.event.events.render.Render2DEvent;
import com.perfect.client.core.event.system.EventListener;
import com.perfect.client.core.event.system.IEventListener;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.utils.RenderUtils;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.Widget;
import com.perfect.client.gui.widgets.common.WindowWidget;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

import java.util.ArrayList;
import java.util.List;

import static com.perfect.client.PerfectClient.clientConfig;
import static com.perfect.client.PerfectClient.mc;


public class WidgetContainer extends Widget implements IEventListener {

	private final List<WindowWidget> windows = new ArrayList<>();

	@Getter
	private boolean escapeEnabled;

	private boolean initialized;

	private boolean active;

	@Setter
	@Getter
	private boolean customScale;

	@Setter
	private boolean hudLayer;

	public WidgetContainer() {
		new WidgetContainerEventListener(this);

		setContainer(() -> this);
		setInteractional(false);
	}

	public WidgetContainer hudLayer() {
		setHudLayer(true);
		return this;
	}

	public WidgetContainer customScale() {
		setCustomScale(true);
		return this;
	}

	public WidgetContainer setMainWidget(Widget mainWidget) {
		addChild(mainWidget);
		return this;
	}

	public void open() {
		if (!active) {
			active = true;
			forceOpen();
		}
	}

	public void close() {
		if (active) {
			active = false;
			UiUtils.setDraggingWidget(null);

			forceClose();

			windows.forEach(WindowWidget::closeIfCloseable);
		}
	}

	public WindowWidget addWindow(WindowWidget windowWidget) {
		windows.remove(windowWidget);

		windows.forEach(WindowWidget::closeIfCloseable);

		addChildFirst(windowWidget);
		windows.add(windowWidget);

		windowWidget.setOnPress(this::setChildFirst);

		return windowWidget;
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		setWidth(DrawConstants.getScreenWidth());
		setHeight(DrawConstants.getScreenHeight());
	}

	private void render(DrawContext context) {
		if (UiUtils.getDraggingWidget() != null && !Managers.KEY_STATE.isLeftPressed()) {
			UiUtils.setDraggingWidget(null);
		}

		escapeEnabled = true;

		Matrix3x2fStack matrices = RenderUtils.expandCapacity(context.getMatrices(), 32);

		if (!customScale) {
			matrices.pushMatrix();
			scale(matrices);
		}

		DrawConstants.updateConstants(context);

		if (!initialized) {
			initialized = true;
		}

		forceReset();

		if (customScale) {
			windows.forEach(window -> window.multiplyScale(clientConfig.getGuiScale()));
		}

		forcePreTick();
		forceAnimationTick();
		forceScale(matrices);
		forceSizeUpdate(matrices);
		forcePostSizeUpdate(matrices);
		forcePositionUpdate(matrices);
		forceOutBoundsCheck();
		forceDisableHover();
		forceUpdateHover();
		forcePreDraw();
		forceDraw(context, matrices);
		forcePostDraw(matrices);

		if (!customScale) {
			matrices.popMatrix();
		}
	}

	private void scale(Matrix3x2fStack matrices) {
		float centerX = DrawConstants.getCenterX();
		float centerY = DrawConstants.getCenterY();

		float size = clientConfig.getGuiScale();

		matrices.translate(centerX, centerY);
		matrices.scale(size, size);
		matrices.translate(-centerX, -centerY);
	}

	public void disableEscape() {
		escapeEnabled = false;
	}

	public static class WidgetContainerEventListener extends EventListener {

		private final WidgetContainer container;

		public WidgetContainerEventListener(WidgetContainer container) {
			this.container = container;
		}

		@Override
		public void onRender2D(Render2DEvent event) {
			if (container.hudLayer && isInWorld()) {
				ifActive(() -> {
					container.render(event.getContext());
				});
			}
		}

		@Override
		public void onGuiRender(Render2DEvent event) {
			if (!container.hudLayer || !isInWorld()) {
				ifActive(() -> container.render(event.getContext()));
			}
		}

		@Override
		public void onKey(KeyEvent event) {
			ifActive(() -> container.forceKey(event));
		}

		@Override
		public void onChar(CharEvent event) {
			ifActive(() -> container.forceChar(event));
		}

		@Override
		public void onMouseButton(MouseButtonEvent event) {
			ifActive(() -> container.forceMouseButton(event));
		}

		@Override
		public void onMouseScroll(MouseScrollEvent event) {
			ifActive(() -> container.forceMouseScroll(event));
		}

		@Override
		public void onMouseMove(MouseMoveEvent event) {
			ifActive(() -> container.forceMouseMove(event));
		}

		private void ifActive(Runnable action) {
			if (container.active) {
				action.run();
			}
		}

		private boolean isInWorld() {
			return mc.world != null && mc.player != null;
		}
	}
}
