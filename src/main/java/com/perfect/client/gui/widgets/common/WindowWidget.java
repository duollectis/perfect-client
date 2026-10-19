package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.animation.Animation;
import com.perfect.client.core.render.animation.easing.Easing;
import com.perfect.client.core.render.renderer.Renderer2D;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.utils.UiUtils;
import com.perfect.client.gui.widgets.common.scrollable.VerticalListWidget;
import lombok.Setter;
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.perfect.client.core.render.font.Fonts.MONTSERRAT_MEDIUM;

public class WindowWidget extends DraggableWidget {

	private static final float PANEL_HEIGHT = 7f;
	private static final float MIN_WIDTH = 10f;

	@Setter
	private float maxHeight = 150.0f;

	private final Animation appearAnimation = new Animation(Easing.IN_OUT_CIRCLE, 250);

	private boolean appearAnimationEnded;

	@Setter
	private Supplier<Float> appearX = () -> DrawConstants.getScreenWidth() / 2.0f;

	@Setter
	private Supplier<Float> appearY = () -> DrawConstants.getScreenHeight() / 2.0f;

	private Supplier<String> nameSupplier;

	@Setter
	private boolean closeable = true;

	@Setter
	private float outlineThickness = 0.8f;

	private boolean shouldClose;

	private Widget holdableWidget;

	private float panelHeight;

	private boolean dragHovered;

	@Setter
	private boolean disablePanel;

	@Setter
	private float indent;

	@Setter
	private Consumer<WindowWidget> onClose;
	@Setter
	private Consumer<WindowWidget> onPress;

	@Setter
	private float backgroundAlpha = 0.8f;

	public WindowWidget(Widget widget) {
		appearAnimation.changeDirection(true);

		setHoldableWidget(widget);
		setActive(true);
	}

	@Override
	public void onOpen() {
		appearAnimation.finish();
	}

	public void closeIfCloseable() {
		if (closeable) {
			close();
		}
	}

	public boolean close() {
		shouldClose = true;
		appearAnimation.changeDirection(false);
		setActive(false);

		return true;
	}

	private void closeInternal() {
		if (holdableWidget != null && closeable) {
			holdableWidget.forceClose();
			holdableWidget.setDestroyed(true);
		}

		setRounding(0.0f);
		indent = -1;
		UiUtils.releaseWidget(this);
		nameSupplier = null;
		setVisible(false);
		disablePanel = false;
		shouldClose = false;

		destroyed = true;

		if (onClose != null) {
			onClose.accept(this);
		}
	}

	public void setHoldableWidget(Widget widget) {
		if (holdableWidget != null && closeable) {
			holdableWidget.setDestroyed(true);
		}

		holdableWidget = addChild(widget);
	}

	@Override
	public void onSizeUpdate(Matrix3x2fStack matrices) {
		panelHeight = getPanelHeight();
		height = panelHeight;

		if (holdableWidget != null) {
			width = holdableWidget.getWidth() + indent * 2;
			height += holdableWidget.getHeight() + indent * 2;
		}

		if (maxHeight > 0.0f) {
			height = Math.min(height, maxHeight);
		}
		width = Math.max(width, MIN_WIDTH);

		if (holdableWidget instanceof VerticalListWidget verticalListWidget) {
			verticalListWidget.setBoundOffset(height - panelHeight - indent * 2);
		}
	}

	private float getPanelHeight() {
		if (disablePanel) {
			return 0f;
		}
		return PANEL_HEIGHT;
	}

	@Override
	public void onPreTick() {
		super.onPreTick();

		if (closeable) {
			container.get().disableEscape();
		}

		if (shouldClose && appearAnimation.isFinished(false)) {
			closeInternal();
		}
	}

	@Override
	public void onAnimationTick() {
		float anim = appearAnimation.get();

		if (!appearAnimationEnded) {
			appearAnimationEnded = appearAnimation.isFinished(true);
		}

		opacity *= anim;
		multiplyScale(anim);
	}

	@Override
	public void onPositionUpdate(Matrix3x2fStack matrices) {
		super.onPositionUpdate(matrices);

		if (!appearAnimationEnded) {
			setPosition(appearX.get() - width / 2.0f, appearY.get() - height / 2.0f);
		}

		if (holdableWidget != null) {
			holdableWidget.setX(indent);
			holdableWidget.setY(panelHeight + indent);
		}
	}

	@Override
	public void onDraw(Matrix3x2fStack matrices) {
		Renderer2D.blurredRound()
		          .pos(drawX, drawY, width, height)
		          .rounding(rounding)
		          .thickness(outlineThickness)
		          .blur(2.0f * opacity)
		          .alpha(backgroundAlpha)
		          .outlineColor(Managers.COLOR.get(Managers.COLOR.DEFAULT_OUTLINE))
		          .color(Managers.COLOR.get(Managers.COLOR.BACKGROUND))
		          .draw(matrices);

		if (!disablePanel && nameSupplier != null) {
			String name = nameSupplier.get();

			if (name != null && !name.isBlank()) {
				MONTSERRAT_MEDIUM.drawCentered(
						matrices,
						name,
						4.0f,
						drawX + width / 2.0f,
						drawY + (panelHeight + indent) / 2.0f,
						Color.WHITE
				);
			}
		}
	}

	@Override
	protected void onUpdateHover() {
		super.onUpdateHover();
		dragHovered = isHovered() && !isContentHovered();
	}

	private boolean isContentHovered() {
		return UiUtils.isHovered(
				mouseX,
				mouseY,
				drawX + indent,
				drawY + panelHeight + indent,
				width - indent * 2.0f,
				height - panelHeight - indent * 2.0f
		);
	}

	@Override
	public void onDisableHover() {
		super.onDisableHover();
		dragHovered = false;
	}

	@Override
	public void onMouseButton(MouseButtonEvent event) {
		super.onMouseButton(event);

		if (!event.isPress()) {
			return;
		}
		if (shouldDraw() && !isHovered()) {
			closeIfCloseable();
		}
		else if (onPress != null && isHovered()) {
			onPress.accept(this);
		}
	}

	@Override
	public void onKey(KeyEvent event) {
		if (event.isPress() && event.getValue() == GLFW.GLFW_KEY_ESCAPE) {
			closeIfCloseable();
		}
	}

	@Override
	public boolean isDragHovered() {
		return dragHovered;
	}

	@Override
	public boolean forceUpdateHover() {
		if (super.forceUpdateHover()) {
			return true;
		}
		return isHovered();
	}

	@Override
	public boolean isAnimationFinished() {
		return appearAnimation.isFinished(false);
	}

	@Override
	public float getAnimationMultiplier() {
		return super.getAnimationMultiplier();
	}

	public void setName(Supplier<String> nameSupplier) {
		this.nameSupplier = nameSupplier;
	}
}
