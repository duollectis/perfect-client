package com.perfect.client.gui.widgets.common;

import com.perfect.client.core.event.events.input.keyboard.CharEvent;
import com.perfect.client.core.event.events.input.keyboard.KeyEvent;
import com.perfect.client.core.event.events.input.mouse.MouseButtonEvent;
import com.perfect.client.core.event.events.input.mouse.MouseMoveEvent;
import com.perfect.client.core.event.events.input.mouse.MouseScrollEvent;
import com.perfect.client.core.manager.Managers;
import com.perfect.client.core.render.renderer.OpacityStack;
import com.perfect.client.core.render.renderer.ScissorStack;
import com.perfect.client.core.utils.widget.Button;
import com.perfect.client.gui.DrawConstants;
import com.perfect.client.gui.WidgetContainer;
import com.perfect.client.gui.utils.*;
import com.perfect.client.gui.utils.Point;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import org.joml.Matrix3x2fStack;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

@Getter
@Setter
public abstract class Widget {

	private final List<Widget> children = new CopyOnWriteArrayList<>();
	protected final Button button = new Button();

	private boolean invisible;
	private boolean inactive;

	protected boolean destroyed;

	protected boolean inBounds;

	protected Supplier<WidgetContainer> container;
	protected Widget parent;

	private float x;
	private float y;
	protected float drawX;
	protected float drawY;
	protected float width;
	protected float height;

	protected float mouseX;
	protected float mouseY;

	protected Supplier<Float> scaleCenterX = () -> drawX + width / 2.0f;
	protected Supplier<Float> scaleCenterY = () -> drawY + height / 2.0f;

	protected Color color = Color.WHITE;

	protected float opacity;
	protected float scale;

	private boolean scaled;

	protected Rounding rounding = Rounding.none();

	private boolean interactional = true;

	public void setRounding(float rounding) {
		this.rounding = Rounding.full(rounding);
	}

	@SuppressWarnings("UnusedReturnValue")
	public <T extends Widget> T addChild(T widget) {
		if (widget == null) {
			return null;
		}
		widget.setContainer(() -> container.get());
		widget.setParent(this);
		children.add(widget);
		return widget;
	}

	@SuppressWarnings("UnusedReturnValue")
	public <T extends Widget> T addChildFirst(T widget) {
		if (widget == null) {
			return null;
		}
		widget.setContainer(() -> container.get());
		widget.setParent(this);
		children.addFirst(widget);
		return widget;
	}

	@SuppressWarnings("UnusedReturnValue")
	public <T extends Widget> T setChildFirst(T widget) {
		if (widget == null) {
			return null;
		}
		if (children.remove(widget)) {
			children.addFirst(widget);
		}
		return widget;
	}

	@SuppressWarnings("unchecked")
	public <T extends Widget> T color(Color color) {
		this.color = color;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Widget> T width(float width) {
		this.width = width;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Widget> T height(float height) {
		this.height = height;
		return (T) this;
	}

	public float getWidth() {
		return Math.max(width, 0);
	}

	public float getHeight() {
		return Math.max(height, 0);
	}

	public boolean shouldDraw() {
		return !invisible;
	}

	public boolean shouldHandleEvents() {
		return !inactive;
	}

	public void setVisible(boolean visible) {
		this.invisible = !visible;

		if (invisible) {
			inactive = true;
		}
	}

	public void setActive(boolean active) {
		this.inactive = !active;

		if (active) {
			invisible = false;
		}
	}

	public boolean isHovered() {
		return button.isHovered();
	}

	public boolean isOverlayHovered() {
		return interactional ? isHovered() : isChildOverlayHovered();
	}

	private boolean isChildOverlayHovered() {
		for (Widget widget : getChildren()) {
			if (widget.isOverlayHovered()) {
				return true;
			}
		}
		return false;
	}

	public void forceAnimationTick() {
		onAnimationTick();
		getChildren().forEach(Widget::forceAnimationTick);
	}

	public void forceOutBoundsCheck() {
		if (parent == null) {
			inBounds = BoxUtils.isIn(
					0,
					0,
					DrawConstants.getScreenWidth(),
					DrawConstants.getScreenHeight(),
					drawX,
					drawY,
					width,
					height
			);
		}
		else if (parent.inBounds) {
			Widget _parent = parent;

			do {
				inBounds = BoxUtils.isIn(
						_parent.drawX,
						_parent.drawY,
						_parent.width,
						_parent.height,
						drawX,
						drawY,
						width,
						height
				);

				_parent = _parent.parent;
			}
			while (_parent != null && inBounds);

		}
		else {
			inBounds = false;
		}

		children.forEach(Widget::forceOutBoundsCheck);
	}

	public void forcePositionUpdate(Matrix3x2fStack matrices) {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			onPositionUpdate(matrices);
			children.forEach(widget -> widget.forcePositionUpdate(matrices));
		}
	}

	public void forceSizeUpdate(Matrix3x2fStack matrices) {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			children.forEach(widget -> widget.forceSizeUpdate(matrices));
			onSizeUpdate(matrices);
		}
	}

	public void forcePostSizeUpdate(Matrix3x2fStack matrices) {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			children.forEach(widget -> widget.forcePostSizeUpdate(matrices));
			onPostSizeUpdate(matrices);
		}
	}

	public void forcePreTick() {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			children.forEach(Widget::forcePreTick);
			onPreTick();
		}
	}

	public void forceReset() {
		opacity = 1f;
		scale = 1f;

		children.removeIf(widget -> widget.isDestroyed() && widget.isAnimationFinished());

		onReset();
		children.forEach(Widget::forceReset);
	}

	public void forceInit() {
		onInit();
		children.forEach(Widget::forceInit);
	}

	public void forceOpen() {
		button.unPress();

		onOpen();
		children.forEach(Widget::forceOpen);
	}

	public void forceClose() {
		onClose();
		children.forEach(Widget::forceClose);
	}

	public void forcePreDraw() {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			children.forEach(Widget::forcePreDraw);
			onPreDraw();
		}
	}

	public void forceScale(Matrix3x2fStack matrices) {
		boolean scaling = pushMatricesWithScale(matrices);

		Point scaledMouse = MatrixUtils.revertScale(
				matrices,
				Managers.MOUSE.getX(),
				Managers.MOUSE.getY()
		);

		mouseX = scaledMouse.getX();
		mouseY = scaledMouse.getY();

		getChildren().forEach(widget -> widget.forceScale(matrices));

		if (scaling) {
			matrices.popMatrix();
		}
	}

	public boolean pushMatricesWithScale(Matrix3x2fStack matrices) {
		if (scale != 1.0f) {
			matrices.pushMatrix();

			float xCenter = scaleCenterX.get();
			float yCenter = scaleCenterY.get();

			matrices.translate(xCenter, yCenter);
			matrices.scale(scale, scale);
			matrices.translate(-xCenter, -yCenter);

			return true;
		}

		return false;
	}

	public void forceDraw(DrawContext context, Matrix3x2fStack matrices) {
		if (!inBounds) {
			return;
		}

		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			OpacityStack.push(opacity);

			boolean scaling = pushMatricesWithScale(matrices);

			if (parent != null) {
				ScissorStack.pushScissor(matrices, parent.drawX, parent.drawY, parent.width, parent.height);
			}

			onDraw(matrices);
			onDraw(context, matrices);

			for (int i = children.size() - 1; i >= 0; i--) {
				children.get(i).forceDraw(context, matrices);
			}

			onPostChildrenDraw(matrices);

			if (parent != null) {
				ScissorStack.popScissor(matrices);
			}

			OpacityStack.pop();

			if (scaling) {
				matrices.popMatrix();
			}
		}
	}

	public void multiplyScale(float val) {
		scale *= val;
	}

	public void forcePostDraw(Matrix3x2fStack matrices) {
		if (shouldHandleEvents() || shouldDraw() || !isAnimationFinished()) {
			children.forEach((widget) -> widget.forcePostDraw(matrices));
			onPostDraw(matrices);
		}
	}

	public boolean forceMouseScroll(MouseScrollEvent event) {
		if (inBounds && shouldHandleEvents()) {
			for (Widget widget : children) {
				if (widget.forceMouseScroll(event) || event.isCancelled()) {
					return true;
				}
			}

			onMouseScroll(event);
		}

		return false;
	}

	public boolean forceMouseButton(MouseButtonEvent event) {
		if (inBounds && shouldHandleEvents()) {
			for (Widget widget : children) {
				if (widget.forceMouseButton(event) || event.isCancelled()) {
					return true;
				}
			}

			onMouseButton(event);
		}

		return false;
	}

	public boolean forceMouseMove(MouseMoveEvent event) {
		if (inBounds && shouldHandleEvents()) {
			for (Widget widget : children) {
				if (widget.forceMouseMove(event) || event.isCancelled()) {
					return true;
				}
			}

			onMouseMove(event);
		}

		return false;
	}

	public boolean forceKey(KeyEvent event) {
		if (inBounds && shouldHandleEvents()) {
			for (Widget widget : children) {
				if (widget.forceKey(event) || event.isCancelled()) {
					return true;
				}
			}

			onKey(event);
		}

		return false;
	}

	public boolean forceChar(CharEvent event) {
		if (inBounds && shouldHandleEvents()) {
			for (Widget widget : children) {
				if (widget.forceChar(event) || event.isCancelled()) {
					return true;
				}
			}

			onChar(event);
		}

		return false;
	}

	public void forceDisableHover() {
		onDisableHover();
		children.forEach(Widget::forceDisableHover);
	}

	public boolean forceUpdateHover() {
		if (inBounds && shouldHandleEvents() && shouldDraw() && (parent == null || parent.isHovered())) {
			onUpdateHover();

			for (Widget widget : children) {
				if (widget.forceUpdateHover()) {
					return true;
				}
			}
		}

		return false;
	}

	public void onPositionUpdate(Matrix3x2fStack matrices) {
		drawX = x;
		drawY = y;

		if (parent != null) {
			drawX += parent.drawX;
			drawY += parent.drawY;
		}
	}

	public final String getDescription() {
		String description = onGetDescription();

		for (Widget widget : getChildren()) {
			if (!widget.inactive && widget.isHovered()) {
				String childDescription = widget.getDescription();

				if (childDescription != null) {
					description = childDescription;
				}
			}
		}

		return description;
	}

	protected String onGetDescription() {
		return null;
	}

	public void onDisableHover() {
		button.setHovered(false);
	}

	protected void onUpdateHover() {
		button.setHovered(UiUtils.isHovered(mouseX, mouseY, drawX, drawY, width, height));
	}

	public void onSizeUpdate(Matrix3x2fStack matrices) {}

	public void onPostSizeUpdate(Matrix3x2fStack matrices) {}

	public void onAnimationTick() {}

	public void onPreTick() {}

	public void onMouseButton(MouseButtonEvent event) {}

	public void onMouseScroll(MouseScrollEvent event) {}

	public void onMouseMove(MouseMoveEvent event) {}

	public void onKey(KeyEvent event) {}

	public void onChar(CharEvent event) {}

	public void onPreDraw() {}

	public void onDraw(Matrix3x2fStack matrices) {}

	public void onDraw(DrawContext context, Matrix3x2fStack matrices) {}

	public void onPostChildrenDraw(Matrix3x2fStack matrices) {}

	public void onPostDraw(Matrix3x2fStack matrices) {}

	public void onReset() {}

	public void onInit() {}

	public void onOpen() {}

	public void onClose() {}

	public float getAnimationMultiplier() {
		return 1f;
	}

	public void setAnimationDirection(boolean direction) {
	}

	public boolean isAnimationFinished() {
		return true;
	}
}
